package tech.adelemphii.skynet.discord.forumscraper.utility;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tech.adelemphii.skynet.Skynet;
import tech.adelemphii.skynet.discord.forumscraper.objects.Author;
import tech.adelemphii.skynet.discord.forumscraper.objects.ForumScraperServer;
import tech.adelemphii.skynet.discord.forumscraper.objects.Topic;
import tech.adelemphii.skynet.discord.forumscraper.objects.TopicType;
import tech.adelemphii.skynet.discord.forumscraper.objects.exceptions.ScrapeException;
import tech.adelemphii.skynet.discord.global.objects.Server;
import tech.adelemphii.skynet.discord.global.utility.data.ServerConfiguration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ScrapeUtility {

    public static MessageEmbed createTopicEmbed(Topic topic, Color color) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(topic.getTitle(), topic.getUrl());
        builder.setColor(color);
        builder.setAuthor(topic.getAuthor().getName(), topic.getAuthor().getUrl(), topic.getAuthor().getImage());

        TemporalAccessor accessor = Instant.ofEpochMilli(topic.getPostDate().getMillis());
        builder.setTimestamp(accessor);

        builder.addField("Comment Count", topic.getCommentCount() + "", true);

        builder.addField("Time Posted", TimeFormat.RELATIVE.format(topic.getPostDate().getMillis()), true);

        builder.setFooter(topic.getTopicType().getName());

        return builder.build();
    }

    public static MessageEmbed createStatusEmbed(Topic topic, Color color) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(topic.getAuthor().getName(), topic.getUrl());
        builder.setColor(color);

        builder.setDescription(topic.getTitle());

        TemporalAccessor accessor = Instant.ofEpochMilli(topic.getPostDate().getMillis());
        builder.setTimestamp(accessor);

        builder.addField("Reply Count", topic.getCommentCount() + "", true);
        builder.addField("Time Posted", TimeFormat.RELATIVE.format(topic.getPostDate().getMillis()), true);

        builder.setFooter(topic.getTopicType().getName());

        return builder.build();
    }

    private static void doChecks(ForumScraperServer forumScraperServer, Guild guild, ArrayList<Topic> topicList) throws ScrapeException {
        if(guild == null) {
            throw new ScrapeException("GUILD IS NULL");
        }

        if(forumScraperServer == null) {
            throw new ScrapeException("FORUMSCRAPER SERVER IS NULL");
        }

        TextChannel channel = guild.getTextChannelById(forumScraperServer.getPopularTopicsChannel());
        if(channel == null) {
            throw new ScrapeException("POPULAR TOPICS CHANNEL NULL");
        }
        channel = guild.getTextChannelById(forumScraperServer.getLatestTopicsChannel());
        if(channel == null) {
            throw new ScrapeException("LATEST TOPICS CHANNEL NULL");
        }
        channel = guild.getTextChannelById(forumScraperServer.getStatusUpdatesChannel());
        if(channel == null) {
            throw new ScrapeException("STATUS UPDATES CHANNEL NULL");
        }

        if(topicList == null) {
            throw new ScrapeException("TOPICS NOT LOADED");
        }
    }

    private static void doPingChecks(ForumScraperServer forumScraperServer, Guild guild) throws ScrapeException {
        if(forumScraperServer == null) {
            throw new ScrapeException("SERVER IS NULL");
        }

        TextChannel channel = guild.getTextChannelById(forumScraperServer.getPingUpdateChannel());
        if(channel == null) {
            throw new ScrapeException("PING UPDATE CHANNEL NOT SELECTED");
        }
    }

    private static ArrayList<Topic> filterTopics(ArrayList<Topic> topicList, TopicType topicType) {
        ArrayList<Topic> topicsToRemove = new ArrayList<>();
        assert topicList != null;
        for(Topic topic : topicList) {
            if(topic.getTopicType() != topicType) {
                topicsToRemove.add(topic);
            }
        }
        for(Topic topic : topicsToRemove) {
            topicList.remove(topic);
        }

        if(topicType == TopicType.POPULAR_TOPIC) {
            topicList.sort((t1, t2) -> t2.getCommentCount() - t1.getCommentCount());
        } else {
            topicList.sort((t1, t2) -> t2.getPostDate().compareTo(t1.getPostDate()));
        }
        return topicList;
    }

    private static ArrayList<MessageEmbed> filterEmbeds(ArrayList<Topic> topicList) {
        ArrayList<MessageEmbed> embedList = new ArrayList<>();
        for(Topic topic : topicList) {
            embedList.add(topic.getEmbed());
        }
        return embedList;
    }

    public static void sendPopularTopics(Guild guild) throws ScrapeException {
        ServerConfiguration serverConfiguration = Skynet.getInstance().getDiscordBot().getServerConfiguration();
        Server server = serverConfiguration.getServer(guild.getIdLong());
        if(server == null) {
            throw new ScrapeException("SERVER IS NULL");
        }
        ForumScraperServer forumScraperServer = server.getForumScraperServer();

        ArrayList<Topic> topicList = scrapeTopics("https://www.lordofthecraft.net/forums/");
        doChecks(forumScraperServer, guild, topicList);

        TextChannel channel = guild.getTextChannelById(forumScraperServer.getPopularTopicsChannel());

        // I don't like doing assertions BUT these are all checked in doChecks
        assert channel != null;
        assert topicList != null;
        topicList = filterTopics(topicList, TopicType.POPULAR_TOPIC);
        ArrayList<MessageEmbed> embedList = filterEmbeds(topicList);

        if(embedList.isEmpty()) {
            String upCheck = upCheck("https://www.lordofthecraft.net/forums/") ? "UP" : "DOWN";
            throw new ScrapeException("NO POPULAR TOPICS FOUND. SITE IS " + upCheck);
        }

        if(forumScraperServer.getPopularTopicMessage() == 0) {
            channel.sendMessageEmbeds(embedList).queue(message -> {
                forumScraperServer.setPopularTopicMessage(message.getIdLong());
                server.setForumScraperServer(forumScraperServer);
                serverConfiguration.addServer(server);
            });
            serverConfiguration.addServer(server);
        } else {
            /*
            channel.retrieveMessageById(forumScraperServer.getPopularTopicMessage())
                    .queue(message -> message.editMessageEmbeds(embedList).queue(),
                    new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (e) -> {
                        forumScraperServer.setPopularTopicMessage(0);
                        server.setForumScraperServer(forumScraperServer);
                        serverConfiguration.addServer(server);


                        channel.sendMessageEmbeds(embedList).queue(message -> {
                            forumScraperServer.setPopularTopicMessage(message.getIdLong());
                            server.setForumScraperServer(forumScraperServer);
                            serverConfiguration.addServer(server);
                        });
                    }));
             */

            channel.retrieveMessageById(forumScraperServer.getPopularTopicMessage())
                    .flatMap(message -> message.editMessageEmbeds(embedList))
                    .queue();
        }
    }

    public static void sendLatestTopics(Guild guild) throws ScrapeException {
        ServerConfiguration serverConfiguration = Skynet.getInstance().getDiscordBot().getServerConfiguration();
        Server server = serverConfiguration.getServer(guild.getIdLong());
        if(server == null) {
            throw new ScrapeException("SERVER IS NULL");
        }
        ForumScraperServer forumScraperServer = server.getForumScraperServer();
        if(forumScraperServer == null) {
            throw new ScrapeException("FORUMSCRAPER SERVER IS NULL");
        }

        String url = "https://www.lordofthecraft.net/forums/";
        ArrayList<Topic> topicList = scrapeTopics(url);
        doChecks(forumScraperServer, guild, topicList);

        TextChannel channel = guild.getTextChannelById(forumScraperServer.getLatestTopicsChannel());

        // I don't like doing assertions BUT these are all checked in doChecks
        assert channel != null;
        assert topicList != null;
        topicList = filterTopics(topicList, TopicType.LATEST_TOPIC);
        ArrayList<MessageEmbed> embedList = filterEmbeds(topicList);

        if(forumScraperServer.getLatestTopicsMessage() == 0) {
            channel.sendMessageEmbeds(embedList).queue(message -> {
                forumScraperServer.setLatestTopicsMessage(message.getIdLong());
                server.setForumScraperServer(forumScraperServer);
            });
            serverConfiguration.addServer(server);
        } else {
            /*
            channel.retrieveMessageById(forumScraperServer.getLatestTopicsMessage())
                    .queue(message -> message.editMessageEmbeds(embedList).queue(),
                    new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (e) -> {
                        forumScraperServer.setLatestTopicsMessage(0);
                        server.setForumScraperServer(forumScraperServer);


                        channel.sendMessageEmbeds(embedList).queue(message -> {
                            forumScraperServer.setLatestTopicsMessage(message.getIdLong());
                            server.setForumScraperServer(forumScraperServer);
                        });
                        serverConfiguration.addServer(server);
                    }));
            */

            channel.retrieveMessageById(forumScraperServer.getLatestTopicsMessage())
                    .flatMap(message -> message.editMessageEmbeds(embedList))
                    .queue();
        }
    }

    public static void sendPingUpdate(Guild guild) throws ScrapeException {
        ServerConfiguration serverConfiguration = Skynet.getInstance().getDiscordBot().getServerConfiguration();
        Server server = serverConfiguration.getServer(guild.getIdLong());
        if(server == null) {
            throw new ScrapeException("SERVER IS NULL");
        }
        ForumScraperServer forumScraperServer = server.getForumScraperServer();
        if(forumScraperServer == null) {
            throw new ScrapeException("FORUMSCRAPER SERVER IS NULL");
        }

        doPingChecks(forumScraperServer, guild);

        String name = "lotc";

        Map<String, EmbedBuilder> embed = FSGeneralUtility.pingServer("mc.lotc.co", name);
        Optional<String> base64Opt = embed.keySet().stream().findFirst();
        String base64 = "";
        if(base64Opt.isPresent()) {
            base64 = base64Opt.get();
        }

        EmbedBuilder serverEmbed = embed.get(base64);
        EmbedBuilder websiteEmbed = FSGeneralUtility.pingWebsite("https://www.lordofthecraft.net/forums/");

        TextChannel channel = guild.getTextChannelById(forumScraperServer.getPingUpdateChannel());

        assert channel != null;
        if(forumScraperServer.getPingUpdateMessage() == 0) {
            FSGeneralUtility.getFileFromCache(name);
            File file = FSGeneralUtility.getFileFromCache(name);
            if(file.exists()) {
                FileUpload fileUpload = FileUpload.fromData(file, name + ".png");
                channel.sendFiles(fileUpload).setEmbeds(List.of(serverEmbed.build(), websiteEmbed.build()))
                        .queue(message -> {
                            forumScraperServer.setPingUpdateMessage(message.getIdLong());
                            server.setForumScraperServer(forumScraperServer);
                            serverConfiguration.addServer(server);
                        });
            } else {
                channel.sendMessageEmbeds(List.of(serverEmbed.build(), websiteEmbed.build()))
                        .queue(message -> {
                            forumScraperServer.setPingUpdateMessage(message.getIdLong());
                            server.setForumScraperServer(forumScraperServer);
                        });
            }
            serverConfiguration.addServer(server);
        } else {
            /*
            channel.retrieveMessageById(forumScraperServer.getPingUpdateMessage())
                    .queue(message -> message.editMessageEmbeds(List.of(serverEmbed.setThumbnail(null).build(), websiteEmbed.build())).queue(),
                    new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (e) -> {
                        forumScraperServer.setPingUpdateMessage(0);
                        server.setForumScraperServer(forumScraperServer);
                        serverConfiguration.addServer(server);


                        channel.sendMessageEmbeds(List.of(serverEmbed.build(), websiteEmbed.build()))
                                .queue(message -> {
                                    forumScraperServer.setPingUpdateMessage(message.getIdLong());
                                    server.setForumScraperServer(forumScraperServer);
                                });
                        serverConfiguration.addServer(server);
                    }));
            */

            channel.retrieveMessageById(forumScraperServer.getPingUpdateMessage())
                    .flatMap(message -> message.editMessageEmbeds(List.of(
                            serverEmbed.setThumbnail(null).build(),
                            websiteEmbed.build())
                    ))
                    .queue();
        }
    }

    public static void sendStatusUpdates(Guild guild) throws ScrapeException {
        ServerConfiguration serverConfiguration = Skynet.getInstance().getDiscordBot().getServerConfiguration();
        Server server = serverConfiguration.getServer(guild.getIdLong());
        if(server == null) {
            throw new ScrapeException("SERVER IS NULL");
        }
        ForumScraperServer forumScraperServer = server.getForumScraperServer();
        if(forumScraperServer == null) {
            throw new ScrapeException("FORUMSCRAPER SERVER IS NULL");
        }

        ArrayList<Topic> topicList = scrapeStatuses("https://www.lordofthecraft.net/forums/");
        doChecks(forumScraperServer, guild, topicList);

        TextChannel channel = guild.getTextChannelById(forumScraperServer.getStatusUpdatesChannel());

        // I don't like doing assertions BUT these are all checked in doChecks
        assert channel != null;
        assert topicList != null;
        topicList = filterTopics(topicList, TopicType.STATUS_UPDATE);
        ArrayList<MessageEmbed> embedList = filterEmbeds(topicList);

        if(forumScraperServer.getStatusUpdatesMessage() == 0) {
            channel.sendMessageEmbeds(embedList).queue(message -> {
                forumScraperServer.setStatusUpdatesMessage(message.getIdLong());
                server.setForumScraperServer(forumScraperServer);
            });
            serverConfiguration.addServer(server);
        } else {
            /*
            channel.retrieveMessageById(forumScraperServer.getStatusUpdatesMessage())
                    .queue(message -> message.editMessageEmbeds(embedList).queue(),
                    new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (e) -> {
                        forumScraperServer.setStatusUpdatesMessage(0);
                        server.setForumScraperServer(forumScraperServer);
                        serverConfiguration.addServer(server);

                        channel.sendMessageEmbeds(embedList).queue(message -> {
                            forumScraperServer.setStatusUpdatesMessage(message.getIdLong());
                            server.setForumScraperServer(forumScraperServer);
                        });
                        serverConfiguration.addServer(server);
                    }));
            */

            channel.retrieveMessageById(forumScraperServer.getStatusUpdatesMessage())
                    .flatMap(message -> message.editMessageEmbeds(embedList))
                    .queue();
        }
    }

    public static boolean upCheck(String url) {
        try {
            Jsoup.connect(url).get();
            return true;
        } catch(IOException e) {
            return false;
        }
    }

    public static ArrayList<Topic> scrapeStatuses(String url) throws ScrapeException {
        boolean upCheck = upCheck(url);
        if(!upCheck) {
            throw new ScrapeException("WEBSITE IS DOWN: " + url);
        }

        ArrayList<Topic> topics = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url).get();
            Elements listElements = document.getElementsByClass("ipsWidget ipsWidget_vertical cAdvancedStatusUpdatesWidget");

            Element first = listElements.first();
            if(first == null) {
                return null;
            }

            listElements = first.getElementsByClass("ipsDataItem");

            for(Element topicElement : listElements) {

                Element authorElement = topicElement.getElementsByClass("ipsType_medium ipsType_reset").get(0);

                Element authorNameElement = authorElement.getElementsByClass("ipsType_break").get(0);
                String profileName = authorNameElement.text();

                String profileLink = authorNameElement.attr("abs:href");
                Author author = new Author(profileName, null, profileLink);

                Element textElement = topicElement.getElementsByClass("ipsType_richText").get(0);
                String text = textElement.text();

                String replyCount = topicElement.attr("abs:data-commentcount")
                        .replace("https://www.lordofthecraft.net/forums/", "");

                int commentAmount = Integer.parseInt(replyCount);

                Element infoElement = topicElement.getElementsByClass("ipsType_blendLinks ipsPos_middle").get(0);
                Element topicLinkElement = infoElement.getElementsByAttribute("href").get(0);

                String topicLink = topicLinkElement.attr("abs:href");

                Element timeElement = topicElement.getElementsByAttribute("datetime").get(0);

                String postTime = timeElement.attr("title");
                DateTime posTime = DateTimeFormat.forPattern("MM/dd/yy hh:mm  a").withZone(DateTimeZone.UTC).parseDateTime(postTime);

                Topic topic = new Topic(text, topicLink, posTime, author, TopicType.STATUS_UPDATE, commentAmount);
                topic.setEmbed(createStatusEmbed(topic, Color.PINK));

                topics.add(topic);
            }
            return topics;
        } catch (IOException e) {
            throw new ScrapeException("ERROR SCRAPING STATUS UPDATES: " + e.getMessage());
        }
    }

    public static ArrayList<Topic> scrapeTopics(String url) throws ScrapeException {
        boolean upCheck = upCheck(url);
        if(!upCheck) {
            throw new ScrapeException("WEBSITE IS DOWN: " + url);
        }

        ArrayList<Topic> topics = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url).get();
            Elements listElements = document.getElementsByClass("ipsDataList ipsDataList_reducedSpacing ipsPad_half");

            int i = 1;
            for(Element listSection : listElements) {

                Elements dataItems = listSection.getElementsByClass("ipsDataItem");

                for(Element topicElement : dataItems) {

                    Element authorElement = topicElement.getElementsByClass("ipsDataItem_icon ipsPos_top").get(0);
                    Element linkElement = authorElement.getElementsByAttribute("href").get(0);
                    Element imageElement = authorElement.getElementsByAttribute("src").get(0);

                    String profileLink = linkElement.attr("abs:href");
                    String imageLink = imageElement.attr("abs:src");
                    String authorName = imageElement.attr("alt");
                    Author author = new Author(authorName, imageLink, profileLink);

                    Element commentElement = topicElement.getElementsByClass("ipsDataItem_main cWidgetComments").get(0);
                    Element topicCommentElement = commentElement.getElementsByAttribute("title").get(0);

                    String commentAmount = topicCommentElement.text();

                    Element infoElement = topicElement.getElementsByClass("ipsType_break ipsContained").get(0);
                    Element topicLinkElement = infoElement.getElementsByAttribute("href").get(0);
                    Element topicNameElement = infoElement.getElementsByClass("ipsDataItem_title").get(0);

                    String topicName = topicNameElement.text();
                    String topicLink = topicLinkElement.attr("abs:href");

                    Element timeElement = topicElement.getElementsByAttribute("datetime").get(0);

                    String postTime = timeElement.attr("title");
                    DateTime posTime = DateTimeFormat.forPattern("MM/dd/yy hh:mm  a").withZone(DateTimeZone.UTC).parseDateTime(postTime);

                    TopicType topicType;
                    if(i == 1) {
                        topicType = TopicType.POPULAR_TOPIC;
                    } else {
                        topicType = TopicType.LATEST_TOPIC;
                    }

                    Topic topic = new Topic(topicName, topicLink, posTime, author, topicType, Integer.parseInt(commentAmount));
                    MessageEmbed embed = createTopicEmbed(topic, Color.PINK);
                    topic.setEmbed(embed);

                    topics.add(topic);
                }
                i++;
            }
            return topics;
        } catch (IOException e) {
            throw new ScrapeException("ERROR SCRAPING TOPICS: " + e.getMessage());
        }
    }
}
