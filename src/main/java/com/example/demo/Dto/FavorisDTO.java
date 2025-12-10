package com.example.demo.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class FavorisDTO {
    private Long userId;
    private List<UUID> activites;
}