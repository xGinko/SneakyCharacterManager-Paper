package net.sneakycharactermanager.bungee.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.config.ServerInfo;
import net.sneakycharactermanager.bungee.Character;
import net.sneakycharactermanager.bungee.SneakyCharacterManager;

import java.io.IOException;
import java.util.List;

public class PaperMessagingUtil {

    public static void sendByteArray(ServerInfo server, String subChannelName, Object... objects) {
        try {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(subChannelName);

            for (Object object : objects) {
                writeObject(out, object);
            }

            server.sendData("sneakymouse:" + SneakyCharacterManager.IDENTIFIER, out.toByteArray());
        } catch (IOException e) {
            SneakyCharacterManager.getInstance().getLogger().throwing("PaperMessagingUtil", "sendByteArray", e);
        }
    }

    private static void writeObject(ByteArrayDataOutput out, Object object) throws IOException {
        if (object instanceof Boolean bool) out.writeBoolean(bool);
        else if (object instanceof Byte b) out.writeByte(b);
        else if (object instanceof Double d) out.writeDouble(d);
        else if (object instanceof Float f) out.writeFloat(f);
        else if (object instanceof Integer i) out.writeInt(i);
        else if (object instanceof Long l) out.writeLong(l);
        else if (object instanceof Short s) out.writeShort(s);
        else if (object instanceof String str) out.writeUTF(str);
        else if (object instanceof Character character) {
            out.writeUTF(character.getUUID());
            out.writeUTF(character.getName());
            out.writeUTF(character.getSkin());
            out.writeBoolean(character.isSlim());
            out.writeInt(character.getTags().size());
            character.getTags().forEach(out::writeUTF);
        }
        else if (object instanceof List<?> list) {
            out.writeInt(list.size());
            for (Object listObject : list) writeObject(out, listObject);
        }
        else throw new IOException("Don't know how to write unidentified Object '" + object.getClass() + "' to ByteArray.");
    }
}
