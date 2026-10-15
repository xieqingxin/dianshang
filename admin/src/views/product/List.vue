<template>
  <div>
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="搜索商品名称" clearable style="width:220px" @keyup.enter="load" />
      <el-select v-model="query.categoryId" placeholder="分类" clearable style="width:160px" @change="load">
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-select v-model="query.originPlace" placeholder="产地" clearable style="width:140px" @change="load">
        <el-option label="广东" value="广东" />
        <el-option label="海南" value="海南" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="openEdit(null)">+ 新增商品</el-button>
    </div>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column label="图片" width="80">
        <template #default="{ row }">
          <el-image :src="row.mainImage" fit="cover" style="width:48px;height:48px;border-radius:6px">
            <template #error><span style="font-size:24px">🍍</span></template>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" min-width="140" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column prop="originPlace" label="产地" width="80" />
      <el-table-column prop="spec" label="规格" width="90" />
      <el-table-column prop="price" label="单价" width="90" />
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="onOff(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button size="small" @click="openStock(row)">库存</el-button>
          <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination layout="total, prev, pager, next" :total="total" :page-size="query.size"
                     :current-page="query.page" @current-change="onPageChange" />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialog" :title="form.id ? '编辑商品' : '新增商品'" width="640px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="商品名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" style="width:100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="产地">
          <div class="row">
            <el-select v-model="form.originPlace" style="width:140px">
              <el-option label="广东" value="广东" />
              <el-option label="海南" value="海南" />
            </el-select>
            <el-input v-model="form.originDetail" placeholder="产地详细，如 广东·湛江" />
          </div>
        </el-form-item>
        <el-form-item label="规格"><el-input v-model="form.spec" placeholder="如 5斤装" /></el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :precision="2" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="商品图片">
          <div class="upload-row">
            <el-upload :show-file-list="false" :http-request="doUpload" accept="image/*">
              <el-button>上传图片</el-button>
            </el-upload>
            <el-image v-if="form.mainImage" :src="form.mainImage" fit="cover" style="width:80px;height:80px;border-radius:6px" />
          </div>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="属性">
          <el-checkbox v-model="form.isSeasonal" :true-value="1" :false-value="0">时令</el-checkbox>
          <el-checkbox v-model="form.isHot" :true-value="1" :false-value="0">热销</el-checkbox>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="上架" inactive-text="下架" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <!-- 库存弹窗 -->
    <el-dialog v-model="stockDialog" title="修改库存" width="360px">
      <el-input-number v-model="stockValue" :min="0" :step="1" />
      <template #footer>
        <el-button @click="stockDialog = false">取消</el-button>
        <el-button type="primary" @click="saveStock">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductPage, getCategoryList, addProduct, updateProduct, deleteProduct, onoffProduct, updateStock, uploadImage } from '../../api'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const categories = ref([])
const query = reactive({ page: 1, size: 10, keyword: '', categoryId: null, originPlace: '' })

const dialog = ref(false)
const form = reactive({ id: null, categoryId: null, name: '', originPlace: '广东', originDetail: '', spec: '', price: 0, stock: 0, mainImage: '', description: '', isSeasonal: 0, isHot: 0, status: 1 })

const stockDialog = ref(false)
const stockId = ref(null)
const stockValue = ref(0)

onMounted(async () => {
  categories.value = await getCategoryList()
  load()
})

async function load() {
  loading.value = true
  try {
    const data = await getProductPage(query)
    list.value = data.records
    total.value = Number(data.total)
  } finally {
    loading.value = false
  }
}
function onPageChange(p) {
  query.page = p
  load()
}

function openEdit(row) {
  if (row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, { id: null, categoryId: categories.value[0]?.id, name: '', originPlace: '广东', originDetail: '', spec: '', price: 0, stock: 0, mainImage: '', description: '', isSeasonal: 0, isHot: 0, status: 1 })
  }
  dialog.value = true
}

async function save() {
  if (!form.name || !form.categoryId) return ElMessage.warning('请填写名称和分类')
  if (form.id) await updateProduct(form)
  else await addProduct(form)
  ElMessage.success('保存成功')
  dialog.value = false
  load()
}

async function onOff(row) {
  const status = row.status === 1 ? 0 : 1
  await onoffProduct(row.id, status)
  ElMessage.success(status === 1 ? '已上架' : '已下架')
  load()
}

function openStock(row) {
  stockId.value = row.id
  stockValue.value = row.stock
  stockDialog.value = true
}
async function saveStock() {
  await updateStock(stockId.value, stockValue.value)
  ElMessage.success('库存已更新')
  stockDialog.value = false
  load()
}

async function onDelete(row) {
  await ElMessageBox.confirm(`确定删除「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteProduct(row.id)
  ElMessage.success('已删除')
  load()
}

async function doUpload(opt) {
  const url = await uploadImage(opt.file)
  form.mainImage = url
  ElMessage.success('上传成功')
}
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
.row {
  display: flex;
  gap: 8px;
  width: 100%;
}
.upload-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>