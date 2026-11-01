package com.console.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.admin.dto.req.MediaReq;
import com.console.admin.entity.Media;
import com.console.admin.service.MediaService;
import com.console.framework.domain.Result;
import com.console.framework.request.PreAuthorize;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/media")
public class MediaController {
    @Resource
    private MediaService mediaService;

    /**
     * 新增媒体信息
     */
    @PostMapping
//    @PreAuthorize(value = "media:add",accountType = 1)
    public Result addMedia(@RequestBody @Validated MediaReq.CreateMediaReq createMediaReq) {
        return mediaService.addMedia(createMediaReq) ? Result.success() : Result.failed();
    }

    /**
     * 删除媒体信息
     */
    @DeleteMapping
//    @PreAuthorize(value = "media:del",accountType = 1)
    public Result delMediaById(@RequestBody MediaReq.DelMediaReq delMediaReq) {
        return mediaService.delMedia(delMediaReq) ? Result.success() : Result.failed();
    }

    @PutMapping("/{part}")
    @PreAuthorize(value = "media:update",accountType = 1)
    public Result updateMedia(@PathVariable String part, @RequestBody @Validated MediaReq.UpdateMediaReq updateMediaReq) {
        return mediaService.updateMedia(part, updateMediaReq) ? Result.success() : Result.failed();
    }

    @GetMapping
    public Result getMediaPage(Page<Media> page, MediaReq.SearchMediaReq searchMediaReq) {
        return Result.success(mediaService.getMediaPage(page,searchMediaReq));
    }
}
