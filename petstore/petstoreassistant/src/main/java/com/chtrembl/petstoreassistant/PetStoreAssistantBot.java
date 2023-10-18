// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.chtrembl.petstoreassistant;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codepoetics.protonpack.collectors.CompletableFutures;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.bot.builder.ActivityHandler;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.ChannelAccount;

/**
 * This class implements the functionality of the Bot.
 *
 * <p>
 * This is where application specific logic for interacting with the users would
 * be added. For this
 * sample, the {@link #onMessageActivity(TurnContext)} echos the text back to
 * the user. The {@link
 * #onMembersAdded(List, TurnContext)} will send a greeting to new conversation
 * participants.
 * </p>
 */
public class PetStoreAssistantBot extends ActivityHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PetStoreAssistantBot.class);

    @Override
    protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
        LOGGER.info("channel data: " + turnContext.getActivity().getChannelData() != null ? turnContext.getActivity().getChannelData().toString() : "null");

        String text = turnContext.getActivity().getText().toLowerCase();
        String digitalPersonResponse = null;
        if(text.contains("ball")){
            String jsonString = "{\"type\":\"image\",\"id\":\"image-ball\",\"data\":{\"url\": \"https://raw.githubusercontent.com/chtrembl/staticcontent/master/dog-toys/ball.jpg?raw=true\",\"alt\": \"This is a ball\"}}";
            Attachment attachment = new Attachment();
            attachment.setContentType("application/json");

            attachment.setContent(new Gson().fromJson(jsonString, JsonObject.class));
            attachment.setName("public-image-ball");


                    return turnContext.sendActivity(
            MessageFactory.attachment(attachment, "I have something nice to show @showcards(image-ball) you.")
        ).thenApply(sendResult -> null);
        }

        return turnContext.sendActivity(
            MessageFactory.text("You said: " + turnContext.getActivity().getText() +  " channeldata: " + turnContext.getActivity().getChannelData() != null ? turnContext.getActivity().getChannelData().toString() : "null" + " entities: " +  turnContext.getActivity().getEntities() != null ? turnContext.getActivity().getEntities().toString() : "null")
        ).thenApply(sendResult -> null);
    }

    @Override
    protected CompletableFuture<Void> onMembersAdded(
            List<ChannelAccount> membersAdded,
            TurnContext turnContext) {
        return membersAdded.stream()
                .filter(
                        member -> !StringUtils
                                .equals(member.getId(), turnContext.getActivity().getRecipient().getId()))
                .map(channel -> turnContext
                        .sendActivity(MessageFactory.text("Hello and welcome to the Azure Pet Store, How can I help you?")))
                .collect(CompletableFutures.toFutureList()).thenApply(resourceResponses -> null);
    }
}
