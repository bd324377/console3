package com.console.core.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.entity.Picture;
import com.console.core.service.PictureService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/picture")
public class PictureController {
    @Resource
    private PictureService pictureService;

    @PostMapping
    public Result addPicture(@RequestParam("file") MultipartFile file) {
        return Result.success(pictureService.addPicture(file));
    }

    /**
     * 分页获取图片列表
     * @param page      分页信息
     * @param picture   查询条件
     */
    @GetMapping
    public Result getPicturePage(Page<Picture> page, Picture picture) {
        return Result.success(pictureService.getPicturePage(page,picture));
    }

    /**
     * 批量删除已上传图片信息
     * @param pictureIds    待删除图片ID
     */
    @DeleteMapping("/{pictureIds}")
    public Result delBatchPictureByIds(@PathVariable Long... pictureIds) {
        return pictureService.delBatchPictureByIds(pictureIds) ? Result.success() : Result.failed();
    }
}
