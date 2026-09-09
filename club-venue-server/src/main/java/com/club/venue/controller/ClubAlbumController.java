package com.club.venue.controller;

import com.club.venue.common.Result;
import com.club.venue.entity.TbClubAlbum;
import com.club.venue.service.TbClubAlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/club-album")
public class ClubAlbumController {

    @Autowired
    private TbClubAlbumService albumService;

    @PostMapping
    public Result<?> upload(@RequestBody TbClubAlbum album) {
        albumService.save(album);
        return Result.ok();
    }

    @GetMapping("/list/{clubId}")
    public Result<List<TbClubAlbum>> listByClub(@PathVariable Long clubId) {
        return Result.ok(albumService.lambdaQuery()
                .eq(TbClubAlbum::getClubId, clubId)
                .orderByDesc(TbClubAlbum::getCreateTime).list());
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        albumService.removeById(id);
        return Result.ok();
    }
}
