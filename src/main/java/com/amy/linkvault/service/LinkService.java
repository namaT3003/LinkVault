package com.amy.linkvault.service;

import com.amy.linkvault.model.Link;
import com.amy.linkvault.repository.LinkRepository;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.List;

import com.amy.linkvault.util.QRCodeGenerator;
import com.google.zxing.WriterException;
import java.io.IOException;

@Service
public class LinkService {

    private final LinkRepository linkRepository;

    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public Link createShortLink(String originalUrl, String customAlias, String category) {

        String finalShortCode;

        if (customAlias != null && !customAlias.isBlank()) {

            if (linkRepository.existsByCustomAlias(customAlias)) {
                throw new RuntimeException("This alias already exists.");
            }

            finalShortCode = customAlias;

        } else {

            finalShortCode = generateShortCode();

            while (linkRepository.existsByShortCode(finalShortCode)) {
                finalShortCode = generateShortCode();
            }
        }

        Link savedLink = new Link(originalUrl, finalShortCode, customAlias, category);

        return linkRepository.save(savedLink);
    }

    private String generateShortCode() {

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        Random random = new Random();

        StringBuilder shortCode = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            shortCode.append(characters.charAt(random.nextInt(characters.length())));
        }

        return shortCode.toString();
    }

    public Link openShortLink(String shortCode) {

    Link savedLink = linkRepository.findByShortCode(shortCode)
            .orElseThrow(() -> new RuntimeException("Short link not found."));

    savedLink.setTotalClicks(savedLink.getTotalClicks() + 1);

    linkRepository.save(savedLink);

    return savedLink;
}

public List<Link> getLinksByCategory(String category) {

    return linkRepository.findByCategoryIgnoreCase(category);

}

public byte[] generateQRCode(String shortCode) throws WriterException, IOException {

    Link link = linkRepository.findByShortCode(shortCode)
            .orElseThrow(() -> new RuntimeException("Short link not found."));

    String shortUrl = "http://localhost:8080/api/links/" + link.getShortCode();

    return QRCodeGenerator.generateQRCode(shortUrl, 300, 300);
}



public Link getStatistics(String shortCode) {

    return linkRepository.findByShortCode(shortCode)
            .orElseThrow(() -> new RuntimeException("Short link not found."));
}

}