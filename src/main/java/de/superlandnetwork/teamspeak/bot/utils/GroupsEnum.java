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

package de.superlandnetwork.teamspeak.bot.utils;

public enum GroupsEnum {


    CAT_STAFF(71, 0, 0),
    CAT_USER(77, 0, 0),
    CAT_EXTRAS(78, 0, 0),
    VERIFY(82, 0, CAT_USER.getId()),
    EXTRAS_RUHE(99, 0, CAT_EXTRAS.getId()),
    EXTRAS_POKE(100, 0, CAT_EXTRAS.getId()),
    EXTRAS_MSG(101, 0, CAT_EXTRAS.getId()),
    EXTRAS_BOT(102, 0, CAT_EXTRAS.getId()),
    USER(VERIFY.getId(), 1, CAT_USER.getId()),
    PREMIUM(VERIFY.getId(), 2, CAT_USER.getId()),
    YOUTUBE(85, 3, CAT_USER.getId()),
    TWITCH(86, 4, CAT_USER.getId()),
    VIP(84, 5, CAT_USER.getId()),
    STAFF(105, 6, CAT_STAFF.getId()),
    BUILDER(106, 7, CAT_STAFF.getId()),
    SUPPORTER(75, 8, CAT_STAFF.getId()),
    MODERATOR(74, 9, CAT_STAFF.getId()),
    DEVELOPER(73, 10, CAT_STAFF.getId()),
    ADMINISTRATOR(72, 11, CAT_STAFF.getId());
    private int id, mcId, cat;

    GroupsEnum(int id, int mcId, int cat) {
        this.id = id;
        this.mcId = mcId;
        this.cat = cat;
    }

    public static int getMcId(int id) {
        for (GroupsEnum e : GroupsEnum.values()) {
            if (e.getId() == id)
                return e.getMcId();
        }
        return 0;
    }

    public static int getId(int mcId) {
        for (GroupsEnum e : GroupsEnum.values()) {
            if (e.getMcId() == mcId)
                return e.getId();
        }
        return 0;
    }

    public static GroupsEnum getEnum(int id) {
        for (GroupsEnum e : GroupsEnum.values()) {
            if (e.getId() == id)
                return e;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public int getMcId() {
        return mcId;
    }

    public int getCat() {
        return cat;
    }

}
