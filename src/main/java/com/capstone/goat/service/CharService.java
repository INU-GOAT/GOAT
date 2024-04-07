package com.capstone.goat.service;

import com.capstone.goat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CharService {
    private final ChatRepository chatRepository;


}
