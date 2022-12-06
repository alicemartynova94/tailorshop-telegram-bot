package com.amartynova.springboot.tailor_shop_demo_bot.service;

import com.amartynova.springboot.tailor_shop_demo_bot.config.BotConfig;
import com.amartynova.springboot.tailor_shop_demo_bot.model.User;
import com.amartynova.springboot.tailor_shop_demo_bot.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    final BotConfig config;
    static  final String HELP_TEXT = "";

    public TelegramBot(BotConfig config){
        this.config = config;
        List<BotCommand> menuList = new ArrayList<>();
        menuList.add(new BotCommand("/start", "get a welcome message"));
        menuList.add(new BotCommand("/help",""));
        menuList.add(new BotCommand("/our price list", ""));
        menuList.add(new BotCommand("/our location", ""));
        menuList.add(new BotCommand("/contact us", ""));
        menuList.add(new BotCommand("/ask a question", ""));
        menuList.add(new BotCommand("/latest works", ""));
        menuList.add(new BotCommand("/settings", "change your settings"));
        menuList.add(new BotCommand("/my data",""));
        menuList.add((new BotCommand("/delete my data","")));
        menuList.add(new BotCommand("/something else", " "));
        try{
            this.execute(new SetMyCommands(menuList, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException e){
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage() && update.getMessage().hasText()){
             String message = update.getMessage().getText();
             long chatId = update.getMessage().getChatId();

             switch(message){
                 case "/start":
                     registerUser(update.getMessage());
                    startCommandRecieved(chatId, update.getMessage().getChat().getFirstName());
                    break;
                 case "/help":
                     sendMessage(chatId, HELP_TEXT);
                     break;
                 default: sendMessage(chatId, "Sorry there is no such command yet!");
             }
        }

    }



    private void registerUser(Message message){
        if(userRepository.findById(message.getChatId()).isEmpty()){
            var chatId = message.getChatId();
            var  chat = message.getChat();
            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("user saved: " + user);
        }
    }
    private void startCommandRecieved(long chatId, String name){

        String answer = "Hi " + name + ", nice to meet you!";
       log.info("Replied to user: " + name);
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        }
        catch(TelegramApiException e){
            log.error("Error occured" + e.getMessage());
        }
    }

}
