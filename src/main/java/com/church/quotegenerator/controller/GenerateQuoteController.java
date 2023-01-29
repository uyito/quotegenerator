package com.church.quotegenerator.controller;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Body;
import com.twilio.type.PhoneNumber;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class GenerateQuoteController {

    private static final String ACCOUNT_SID = "ACa923bd6c24a8c25bbfe6f62690711110";
    private static final String AUTH_TOKEN = "29bb2b1ae5149503948f973cacc1021a";

    // Initialize Twilio client
    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    @PostMapping("/send")
    public String sendTextMessage(@RequestParam("to") String to, @RequestParam("message") String message) {
        PhoneNumber toPhoneNumber = new PhoneNumber(to);
        PhoneNumber fromPhoneNumber = new PhoneNumber("+19377212489");
        Message messageObject = Message.creator(toPhoneNumber, fromPhoneNumber, message).create();
        return messageObject.getSid();
    }

    @GetMapping("/hello")
    public String helloWeb() throws IOException {
        return generator();
    }

    @PostMapping(value = "/sms", produces = MediaType.APPLICATION_XML_VALUE)
    public String sendSms() {
        Body body = new Body.Builder("The Robots are coming! Head for the hills!").build();
        com.twilio.twiml.messaging.Message sms = new com.twilio.twiml.messaging.Message.Builder().body(body).build();
        MessagingResponse twiml = new MessagingResponse.Builder().message(sms).build();
        return twiml.toXml();
    }

    private String generator() throws IOException {
        URL url = new URL("http://quotes.rest/bible/verse.json");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("HTTP error code : " + status);
        }

        System.out.println(con.getResponseMessage() + "gjhsbojsn shvklns");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(String.valueOf(content));

        String verse = json.get("contents").get("verse").asText();
        System.out.println("this is verse: " + verse);
        return verse;
    }
}
