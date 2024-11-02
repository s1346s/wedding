package com.acompany.weddingaffiliate.weddingaffiliate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.acompany.weddingaffiliate.weddingaffiliate.entity.Region;
import com.acompany.weddingaffiliate.weddingaffiliate.repository.RegionRepository;

@Controller
public class WeddingExpoController {

    @Autowired
    private RegionRepository regionRepository;

    @GetMapping("/expos")
    public String getAllExpos(Model model) {
        List<Region> regions = regionRepository.findAll();
        model.addAttribute("regions", regions);
        return "expos"; // templates/expos.html 뷰를 렌더링합니다.
    }
}