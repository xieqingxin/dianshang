package com.ycyx.shop.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ycyx.shop.common.exception.BizException;
import com.ycyx.shop.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 文件上传接口：图片上传到本地服务器
 */
@Tag(name = "通用", description = "图片上传")
@RestController
@RequestMapping("/common")
public class FileController {

    @Value("${ycyx.upload.path}")
    private String uploadPath;

    @Value("${ycyx.upload.url-prefix}")
    private String urlPrefix;

    private static final String[] ALLOWED_EXT = {"jpg", "jpeg", "png", "gif", "webp"};

    @Operation(summary = "上传图片")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException("上传文件不能为空");
        }
        String ext = FileUtil.extName(file.getOriginalFilename());
        if (StrUtil.isBlank(ext) || !isAllowed(ext)) {
            throw new BizException("仅支持 jpg/jpeg/png/gif/webp 格式图片");
        }

        // 按日期分目录存储
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        File dir = new File(uploadPath + File.separator + datePath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new BizException("创建上传目录失败");
        }

        String filename = IdUtil.simpleUUID() + "." + ext;
        try {
            file.transferTo(new File(dir, filename));
        } catch (Exception e) {
            throw new BizException("文件上传失败");
        }

        // 返回可访问 URL（context-path 为 /api）
        String url = "/api" + urlPrefix + datePath + "/" + filename;
        return Result.success(url);
    }

    private boolean isAllowed(String ext) {
        for (String allowed : ALLOWED_EXT) {
            if (allowed.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }
}