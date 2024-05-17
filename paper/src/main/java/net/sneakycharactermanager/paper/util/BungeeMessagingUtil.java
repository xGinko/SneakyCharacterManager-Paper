package net.sneakycharactermanager.paper.util;

import net.sneakycharactermanager.paper.SneakyCharacterManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageRecipient;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class BungeeMessagingUtil {

    public static void sendByteArray(Player requester, String subChannelName, Object... objects) {
        try (ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteArrayOutput)) {

            out.writeUTF(subChannelName + "_UUID:" + UUID.randomUUID());

            for (Object object : objects) {
                writeToOutputStream(out, object);
            }

            SneakyCharacterManager plugin = SneakyCharacterManager.getInstance();
            PluginMessageRecipient recipient = requester == null ? plugin.getServer() : requester;
            recipient.sendPluginMessage(plugin, "sneakymouse:" + SneakyCharacterManager.IDENTIFIER, byteArrayOutput.toByteArray());
        } catch (IOException e) {
            SneakyCharacterManager.logger().error("Failed while sending PluginMessage.", e);
        }
    }

    private static void writeToOutputStream(DataOutputStream out, Object object) throws IOException {
        if (object instanceof Boolean bool) out.writeBoolean(bool);
        else if (object instanceof Byte b) out.writeByte(b);
        else if (object instanceof Double d) out.writeDouble(d);
        else if (object instanceof Float f) out.writeFloat(f);
        else if (object instanceof Integer i) out.writeInt(i);
        else if (object instanceof Long l) out.writeLong(l);
        else if (object instanceof Short s) out.writeShort(s);
        else if (object instanceof String str) out.writeUTF(str);
        else if (object instanceof List<?> list) {
            out.writeInt(list.size());
            for (Object listObject : list) writeToOutputStream(out, listObject);
        }
        else throw new IOException("Don't know how to write unidentified Object '" + object.getClass() + "' to ByteArray.");
    }
}