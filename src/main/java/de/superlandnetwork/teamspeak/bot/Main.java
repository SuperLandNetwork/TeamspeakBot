/*
 * MIT License
 *
 * Copyright (c) 2019 Filli Group (Einzelunternehmen)
 * Copyright (c) 2019 Filli IT (Einzelunternehmen)
 * Copyright (c) 2019 Filli Games (Einzelunternehmen)
 * Copyright (c) 2019 Ursin Filli
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package de.superlandnetwork.teamspeak.bot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.reconnect.ConnectionHandler;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.superlandnetwork.teamspeak.bot.utils.Config;
import de.superlandnetwork.teamspeak.bot.utils.GroupsEnum;
import de.superlandnetwork.teamspeak.bot.utils.MySQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static volatile int clientId;

    public static MySQL mySQL;

    private static String username, password;

    public static void main(String[] args) {
        Properties settings = new Config().getSettingsProps();
        mySQL = new MySQL(settings.getProperty("host"), settings.getProperty("port"), settings.getProperty("database"), settings.getProperty("username"), settings.getProperty("password"));

        username = settings.getProperty("query_username");
        password = settings.getProperty("query_password");

        try {
            mySQL.connect();
            System.out.println("MySQL Connected");
        } catch (SQLException e) {
            System.err.println("MySQL Failed");
            System.exit(1);
        }

        final TS3Config config = new TS3Config();
        config.setHost("ts.superlandnetwork.de");
        config.setEnableCommunicationsLogging(true);
        config.setFloodRate(TS3Query.FloodRate.DEFAULT);
        config.setReconnectStrategy(ReconnectStrategy.exponentialBackoff());

        config.setConnectionHandler(new ConnectionHandler() {
            @Override
            public void onConnect(TS3Query ts3Query) {
                connect(ts3Query.getApi());
            }

            @Override
            public void onDisconnect(TS3Query ts3Query) {
            }
        });

        final TS3Query query = new TS3Query(config);
        query.connect();

        doStuff(query.getAsyncApi());
    }

    private static void connect(TS3Api api) {
        api.login(username, password);
        api.selectVirtualServerById(1, "SuperLandNetwork.de Bot");

        api.registerAllEvents();

        clientId = api.whoAmI().getId();
        log.info("Query Connected.");
    }

    private static void doStuff(final TS3ApiAsync api) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                checkVerifyUsers(api);
            }
        }, 60000L, 1800000L);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sendVerify(api);
            }
        }, 60000L, 60000L);

        api.addTS3Listeners(new TS3Listener() {
            @Override
            public void onTextMessage(TextMessageEvent e) {
                if (e.getTargetMode() != TextMessageTargetMode.CLIENT || e.getInvokerId() == clientId) {
                    return;
                }

                int id = e.getInvokerId();

                String msg = e.getMessage();
                String[] args = msg.split(" ");
                String cmd = args[0];

                if (cmd.startsWith("!")) {
                    api.getClientInfo(id).onSuccess(clientInfo -> {
                        if (cmd.equalsIgnoreCase("!verify")) {
                            api.sendPrivateMessage(id, "\n"
                                    + "»------------------------[Color=grey]=======[/color][Color=skyblue][[/color][b]» Bot «[/b][Color=skyblue]][/color][Color=grey]=======[/color]------------------------«"
                                    + "\n"
                                    + "» [Color=red]How To Verifizieren[/color]: \n"
                                    + "» Du must auf unseren Minecaft Server und dort folgenden Command ausführen /ts set " + clientInfo.getUniqueIdentifier() + "\n"
                                    + "\n"
                                    + "»------------------------[Color=grey]=======[/color][Color=skyblue][[/color][b]» Bot «[/b][Color=skyblue]][/color][Color=grey]=======[/color]------------------------«");
                            //api.sendPrivateMessage(id, "» Zurzeit Deaktiviert!");
                            return;
                        }

                        if (cmd.equalsIgnoreCase("!about")) {
                            if (args[1].equalsIgnoreCase("!toggle")) {
                                api.sendPrivateMessage(id, "\n"
                                        + "»------------------------[Color=grey]=======[/color][Color=skyblue][[/color][b]» Bot «[/b][Color=skyblue]][/color][Color=grey]=======[/color]------------------------«"
                                        + "\n"
                                        + "» [Color=red]Information über !toggle[/color]: \n"
                                        + "» Mit diesem Rang bekommst du beim verbinden des Servers keine Nachricht von mir! \n"
                                        + "\n"
                                        + "»------------------------[Color=grey]=======[/color][Color=skyblue][[/color][b]» Bot «[/b][Color=skyblue]][/color][Color=grey]=======[/color]------------------------«");
                                return;
                            }
                            if (args[1].equalsIgnoreCase("!msg")) {
                                api.sendPrivateMessage(id, "\n"
                                        + "»------------------------[Color=grey]=======[/color][Color=skyblue][[/color][b]» Bot «[/b][Color=skyblue]][/color][Color=grey]=======[/color]------------------------«"
                                        + "\n"
                                        + "» [Color=red]Information über !msg[/color]: \n"
                                        + "» Mit diesem Rang kannst du keine Chat-Nachrichten von anderen Personen erhalten! \n"
                                        + "\n"
                                        + "»------------------------[Color=grey]=======[/color][Color=skyblue][[/color][b]» Bot «[/b][Color=skyblue]][/color][Color=grey]=======[/color]------------------------«");
                                return;
                            }
                            if (args[1].equalsIgnoreCase("!poke")) {
                                api.sendPrivateMessage(id, "\n"
                                        + "»------------------------[Color=grey]=======[/color][Color=skyblue][[/color][b]» Bot «[/b][Color=skyblue]][/color][Color=grey]=======[/color]------------------------«"
                                        + "\n"
                                        + "» [Color=red]Information über !poke[/color]: \n"
                                        + "» Mit diesem Rang kannst du nicht angestupst werden! \n"
                                        + "\n"
                                        + "»------------------------[Color=grey]=======[/color][Color=skyblue][[/color][b]» Bot «[/b][Color=skyblue]][/color][Color=grey]=======[/color]------------------------«");
                                return;
                            }
                        }

                        if (cmd.equalsIgnoreCase("!help")) {
                            if (!clientInfo.isInServerGroup(GroupsEnum.STAFF.getId())) {
                                api.sendPrivateMessage(id, "\n"
                                        + "»------------------------[Color=grey]=======[/color][Color=skyblue][[/color][b]» Bot «[/b][Color=skyblue]][/color][Color=grey]=======[/color]------------------------«"
                                        + "\n"
                                        + "» Mit [color=skyblue]!msg[/color] aktivierst und deaktivierst du den Message-Rang! \n"
                                        + "» Mit [color=skyblue]!poke[/color] aktivierst und deaktivierst du den Poke-Rang! \n"
                                        + "» Mit [color=skyblue]!toggle[/color] aktivierst und deaktivierst du den Toggle-Rang! \n"
                                        + "» Mit [color=skyblue]!about[/color] [color=red]<Befehl>[/color] informierst du dich über einen Befehl! \n"
                                        + "\n"
                                        + "»------------------------[Color=grey]=======[/color][Color=skyblue][[/color][b]» Bot «[/b][Color=skyblue]][/color][Color=grey]=======[/color]------------------------«");
                                return;
                            }

                            api.sendPrivateMessage(id, "\n"
                                    + "»------------------------[Color=grey]=======[/color][Color=skyblue][[/color][b]» Bot «[/b][Color=skyblue]][/color][Color=grey]=======[/color]------------------------«"
                                    + "\n"
                                    + "» Mit [color=skyblue]!msg[/color] aktivierst und deaktivierst du den Message-Rang! \n"
                                    + "» Mit [color=skyblue]!poke[/color] aktivierst und deaktivierst du den Poke-Rang! \n"
                                    + "» Mit [color=skyblue]!toggle[/color] aktivierst und deaktivierst du den Toggle-Rang! \n"
                                    + "» Mit [color=skyblue]!about[/color] [color=red]<Befehl>[/color] informierst du dich über einen Befehl! \n"
                                    + "\n"
                                    + "» Mit [color=skyblue]!ruhe[/color]  aktivierst und deaktivierst du den Ruhe-Rang! \n"
                                    + "» Mit [color=skyblue]!support[/color] [color=red]<toggle/move>[/color] Support Commands! \n"
                                    + "\n"
                                    + "»------------------------[Color=grey]=======[/color][Color=skyblue][[/color][b]» Bot «[/b][Color=skyblue]][/color][Color=grey]=======[/color]------------------------«");
                            return;
                        }

                        /* Verify Only Commands */

                        if (!clientInfo.isInServerGroup(GroupsEnum.VERIFY.getId())) {
                            api.sendPrivateMessage(id, "» Du bist noch nicht verifiziert! Gib [Color=red]!verify[/color] ein um zu erfahren wie man sich verifiziert!");
                            return;
                        }

                        if (cmd.equalsIgnoreCase("!ruhe")) {
                            if (!clientInfo.isInServerGroup(GroupsEnum.STAFF.getId())) {
                                return;
                            }

                            updateGroup(api, GroupsEnum.EXTRAS_RUHE, clientInfo, "» Du hast nun den [color=green]Ruhe[/color]-Rang!", "» Du hast nun nicht mehr den [color=red]Ruhe[/color]-Rang!");
                            return;
                        }

                        if (cmd.equalsIgnoreCase("!poke")) {
                            updateGroup(api, GroupsEnum.EXTRAS_POKE, clientInfo, "» Du hast nun den [color=green]Poke[/color]-Rang!", "» Du hast nun nicht mehr den [color=red]Poke[/color]-Rang!");
                            return;
                        }

                        if (cmd.equalsIgnoreCase("!msg")) {
                            updateGroup(api, GroupsEnum.EXTRAS_MSG, clientInfo, "» Du hast nun den [color=green]Message[/color]-Rang!", "» Du hast nun nicht mehr den [color=red]Message[/color]-Rang!");
                            return;
                        }

                        if (cmd.equalsIgnoreCase("!toggle")) {
                            updateGroup(api, GroupsEnum.EXTRAS_BOT, clientInfo, "» Du hast nun den [color=green]ToggleBot[/color]-Rang!", "» Du hast nun nicht mehr den [color=red]ToggleBot[/color]-Rang!");
//                            return;
                        }
                    });
                    return;
                }

                api.getClientInfo(id).onSuccess(clientInfo -> {
                    if (clientInfo.isServerQueryClient()) return;
                    if (msg.equalsIgnoreCase("abort")) {
                        cancleVerify(clientInfo);
                        api.sendPrivateMessage(clientInfo.getId(), "Verifizierung abgebrochen!");
                        return;
                    }
                    checkVerify(api, clientInfo, msg);
                });
            }

            @Override
            public void onServerEdit(ServerEditedEvent e) {
                //
            }

            @Override
            public void onClientMoved(ClientMovedEvent e) {
                //
            }

            @Override
            public void onClientLeave(ClientLeaveEvent e) {
                // ...
            }

            @Override
            public void onClientJoin(ClientJoinEvent e) {
                int id = e.getClientId();
                if (e.getClientType() == 0) {
                    if (e.getClientNickname().equalsIgnoreCase("TeamSpeakUser")) {
                        api.kickClientFromServer("Bitte Ändere deinen Namen!", id);
                        log.info("User kicked.");
                        return;
                    }

                    api.getClientInfo(id).onSuccess(clientInfo -> {
                        if (!clientInfo.isInServerGroup(GroupsEnum.EXTRAS_BOT.getId())) {
                            api.sendPrivateMessage(id, "\n"
                                    + "»------------------------[Color=grey]=======[/color][Color=skyblue][[/color][b]» Bot «[/b][Color=skyblue]][/color][Color=grey]=======[/color]------------------------« \n"
                                    + "\n"
                                    + "» Willkommen [color=skyblue]" + e.getClientNickname() + "[/color] auf [b][color=Skyblue]SuperLandNetwork.de[/color][/b]! \n"
                                    + "» Benutze [color=skyblue]!verify[/color] um zu erfahren wie man sich verifiziert! \n"
                                    + "» Weitere Befehle siehst du mit [color=skyblue]!help[/color]! \n"
                                    + "\n"
                                    + "» Mit dem Joinen aktzeptierst du unsere Nutzungs sowie Datenschutzbestimmungen. \n"
                                    + "» Weitere Informationen dazu unter [url]https://www.superlandnetwork.de/legal/teamspeak[/url] \n"
                                    + "\n"
                                    + "»------------------------[Color=grey]=======[/color][Color=skyblue][[/color][b]» Bot «[/b][Color=skyblue]][/color][Color=grey]=======[/color]------------------------«");
                            log.info("User Joined.");
                        }
                    });
                }
            }

            @Override
            public void onChannelEdit(ChannelEditedEvent e) {
                // ...
            }

            @Override
            public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent e) {
                // ...
            }

            @Override
            public void onChannelCreate(ChannelCreateEvent e) {
                // ...
            }

            @Override
            public void onChannelDeleted(ChannelDeletedEvent e) {
                // ...
            }

            @Override
            public void onChannelMoved(ChannelMovedEvent e) {
                // ...
            }

            @Override
            public void onChannelPasswordChanged(ChannelPasswordChangedEvent e) {
                // ...
            }

            @Override
            public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent e) {
                // ...
            }
        });
    }

    private static void checkVerifyUsers(TS3ApiAsync api) {
        api.getClients().onSuccess(clients -> {
            for (Client c : clients) {
                if (c.isServerQueryClient()) continue;
                if (c.isInServerGroup(GroupsEnum.VERIFY.getId()))
                    api.getClientInfo(c.getId()).onSuccess(clientInfo -> checkUserRoles(api, clientInfo));
            }
        });
    }

    private static void checkVerify(TS3ApiAsync api, ClientInfo clientInfo, String name) {
        String id = clientInfo.getUniqueIdentifier();
        try {
            String sql = "SELECT name,uuid FROM `sln_verify` WHERE `send` = 1 AND `type` = 1 AND `content` = '" + id + "'";
            ResultSet rs = mySQL.getResult(sql);
            if (rs.next()) {
                if (rs.getString("name").equalsIgnoreCase(name)) {
                    cancleVerify(clientInfo);
                    String sql2 = "UPDATE `sln_users` SET `uid`='" + clientInfo.getUniqueIdentifier() + "' WHERE `uuid`='" + rs.getString("uuid") + "'";
                    mySQL.update(sql2);
                    api.addClientToServerGroup(GroupsEnum.VERIFY.getId(), clientInfo.getDatabaseId());
                    checkUserRoles(api, clientInfo);
                    api.sendPrivateMessage(clientInfo.getId(),"Deine Identität wurde bestätigt");
                } else
                    api.sendPrivateMessage(clientInfo.getId(), "Falscher Minecraft-Name!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void checkUserRoles(TS3ApiAsync api, ClientInfo clientInfo) {
        List<Integer> l = new ArrayList<>();
        try {
            l = getUser(clientInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int group : clientInfo.getServerGroups()) {
            int mcId = GroupsEnum.getMcId(group);
            if (mcId != 0) {
                if (!l.contains(mcId))
                    api.removeClientFromServerGroup(group, clientInfo.getDatabaseId());
            }
        }
        for (int list : l) {
            if (!clientInfo.isInServerGroup(GroupsEnum.getId(list))) {
                api.addClientToServerGroup(GroupsEnum.getId(list), clientInfo.getDatabaseId());
            }
            GroupsEnum e = GroupsEnum.getEnum(GroupsEnum.getId(list));
            if (e == null) continue;
            if (e.getCat() == 0) continue;
            GroupsEnum d = GroupsEnum.getEnum(e.getCat());
            if (d == null) continue;
            if (!clientInfo.isInServerGroup(d.getId())) {
                api.addClientToServerGroup(d.getId(), clientInfo.getDatabaseId());
            }
        }
    }

    private static List<Integer> getUser(ClientInfo clientInfo) throws SQLException {
        List<Integer> groupIds = new ArrayList<>();
        UUID uuid = getUUID(clientInfo.getUniqueIdentifier());
        if (uuid != null) {
            String sql = "SELECT groupId FROM `sln_mc_perm_users` WHERE `deleted_at` IS NULL AND `uuid` = '" + uuid.toString() + "'";
            ResultSet rs = mySQL.getResult(sql);
            while (rs.next()) {
                groupIds.add(rs.getInt("groupId"));
            }
        }
        return groupIds;
    }

    private static UUID getUUID(String id) throws SQLException {
        String sql = "SELECT uuid FROM `sln_users` WHERE `deleted_at` IS NULL AND `uid` = '" + id + "'";
        ResultSet rs = mySQL.getResult(sql);
        if (rs.next())
            return UUID.fromString(rs.getString("uuid"));
        return null;
    }

    private static void sendVerify(TS3ApiAsync api) {
        try {
            String sql = "SELECT id,content FROM `sln_verify` WHERE `send` = 0 AND `type` = 1";
            ResultSet rs = mySQL.getResult(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                api.getClientByUId(rs.getString("content")).onSuccess(clientInfo -> {
                    try {
                        String sql2 = "UPDATE `sln_verify` SET `send` = 1 WHERE `id` = '" + id + "'";
                        mySQL.update(sql2);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    api.sendPrivateMessage(clientInfo.getId(), "Bitte sende deinen Minecraft-Namen in den Chat, um die Verifizierung deiner Identität abzuschliessen!");
                    api.sendPrivateMessage(clientInfo.getId(), "Falls diese Verifizierung nicht von dir stammt sende bitte ''abort'' im Chat!");
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void cancleVerify(ClientInfo clientInfo) {
        String id = clientInfo.getUniqueIdentifier();
        try {
            String sql = "DELETE FROM `sln_verify` WHERE `send` = 1 AND `type` = 1 AND `content` = '" + id + "'";
            mySQL.update(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateGroup(TS3ApiAsync api, GroupsEnum g, ClientInfo c, String text_add, String text_rem) {
        if (c.isInServerGroup(g.getId())) {
            api.removeClientFromServerGroup(g.getId(), c.getDatabaseId());
            if (text_rem != null)
                api.sendPrivateMessage(c.getId(), text_rem);
        } else {
            if (text_add != null)
                api.sendPrivateMessage(c.getId(), text_add);
            api.addClientToServerGroup(g.getId(), c.getDatabaseId());
        }
    }
}
