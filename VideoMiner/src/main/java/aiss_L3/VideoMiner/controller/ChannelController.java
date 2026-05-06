package aiss_L3.VideoMiner.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import aiss_L3.VideoMiner.exception.ChannelNotFoundException;
import aiss_L3.VideoMiner.model.Channel;
import aiss_L3.VideoMiner.repository.ChannelRepository;

@RestController
@RequestMapping("/channels")
public class ChannelController {

    @Autowired
    ChannelRepository channelRepository;


    @GetMapping
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }


    @GetMapping("/{id}")
    public Channel findById(@PathVariable String id) throws ChannelNotFoundException {
        return channelRepository.findById(id)
                .orElseThrow(ChannelNotFoundException::new);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Channel create(@Valid @RequestBody Channel channel) {
        return channelRepository.save(channel);
    }
}