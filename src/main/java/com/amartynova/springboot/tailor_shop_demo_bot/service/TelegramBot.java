package com.amartynova.springboot.tailor_shop_demo_bot.service;

import com.amartynova.springboot.tailor_shop_demo_bot.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    public TelegramBot(BotConfig config){
        this.config = config;
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
                    startCommandRecieved(chatId, update.getMessage().getChat().getFirstName());
                    break;
                 default: sendMessage(chatId, "Sorry there is no such command yet!");
             }
        }

    }

    private void startCommandRecieved(long chatId, String name){

        String answer = "Hi " + name + ", nice to meet you!";
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

        }
    }

}
