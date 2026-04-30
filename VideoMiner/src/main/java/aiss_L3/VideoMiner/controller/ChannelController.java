package aiss_L3.VideoMiner.controller;

import aiss_L3.VideoMiner.exception.ChannelNotFoundException;
import aiss_L3.VideoMiner.model.Channel;
import aiss_L3.VideoMiner.repository.ChannelRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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