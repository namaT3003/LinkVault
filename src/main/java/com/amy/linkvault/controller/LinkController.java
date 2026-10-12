package com.amy.linkvault.controller;

import com.amy.linkvault.model.Link;
import com.amy.linkvault.service.LinkService;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.springframework.http.MediaType;
import com.google.zxing.WriterException;
import java.io.IOException;

@RestController
@RequestMapping("/api/links")
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping("/shorten")
    public Link createShortLink(
            @RequestParam String originalUrl,
            @RequestParam(required = false) String customAlias,
            @RequestParam(required = false) String category) {

        return linkService.createShortLink(originalUrl, customAlias, category);
    }

    @GetMapping("/{shortCode}")
public ResponseEntity<Void> redirectToWebsite(@PathVariable String shortCode) {

    Link savedLink = linkService.openShortLink(shortCode);

    HttpHeaders headers = new HttpHeaders();

    headers.setLocation(URI.create(savedLink.getOriginalUrl()));

    return new ResponseEntity<>(headers, HttpStatus.FOUND);
}

   @GetMapping("/category/{category}")
public List<Link> getLinksByCategory(@PathVariable String category) {

    return linkService.getLinksByCategory(category);

} 

@GetMapping(value = "/{shortCode}/qr", produces = MediaType.IMAGE_PNG_VALUE)
public ResponseEntity<byte[]> generateQRCode(@PathVariable String shortCode)
        throws WriterException, IOException {

    byte[] qrCode = linkService.generateQRCode(shortCode);

    return ResponseEntity
            .ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(qrCode);
}

@GetMapping("/{shortCode}/stats")
public Link getStatistics(@PathVariable String shortCode) {

    return linkService.getStatistics(shortCode);

}



}