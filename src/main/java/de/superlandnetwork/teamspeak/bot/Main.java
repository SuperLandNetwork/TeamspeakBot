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
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static volatile int clientId;

    private static String username, password;

    public static void main(String[] args) {
        username = args[0];
        password = args[1];

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
        api.addTS3Listeners(new TS3Listener() {
            @Override
            public void onTextMessage(TextMessageEvent e) {
                if (e.getTargetMode() != TextMessageTargetMode.CLIENT || e.getInvokerId() == clientId) {
                    return;
                }

                int id = e.getInvokerId();

                String message = e.getMessage();
                String[] args = message.split(" ");
                String cmd = args[0];

                if (cmd.startsWith("!")) {
                    api.getClientInfo(id).onSuccess(clientInfo -> {
                        if (cmd.equalsIgnoreCase("!verify")) {
                            /*
                            api.sendPrivateMessage(id, "\n"
                                    + "»------------------------[Color=grey]=======[/color][Color=skyblue][[/color][b]» Bot «[/b][Color=skyblue]][/color][Color=grey]=======[/color]------------------------«"
                                    + "\n"
                                    + "» [Color=red]How To Verifizieren[/color]: \n"
                                    + "» Du must auf unseren Minecaft Server und dort folgenden Command ausführen /ts set " + c.getUniqueIdentifier() + "\n"
                                    + "\n"
                                    + "»------------------------[Color=grey]=======[/color][Color=skyblue][[/color][b]» Bot «[/b][Color=skyblue]][/color][Color=grey]=======[/color]------------------------«");
                            */
                            api.sendPrivateMessage(id, "» Zurzeit Deaktiviert!");
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
                            if (!clientInfo.isInServerGroup(GroupsEnum.TEAM.getId())) {
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

                        if (!clientInfo.isInServerGroup(GroupsEnum.USER_VERIFY.getId())) {
                            api.sendPrivateMessage(id, "» Du bist noch nicht verifiziert! Gib [Color=red]!verify[/color] ein um zu erfahren wie man sich verifiziert!");
                            return;
                        }

                        if (cmd.equalsIgnoreCase("!ruhe")) {
                            if (!clientInfo.isInServerGroup(GroupsEnum.TEAM.getId())) {
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
                            return;
                        }
                    });
                }
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
