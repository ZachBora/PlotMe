package com.worldcretornica.plotme.utils;

import java.io.Reader;
import java.io.InputStreamReader;
import org.json.simple.JSONObject;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.HashMap;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.json.simple.parser.JSONParser;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.Callable;

public class NameFetcher implements Callable<Map<UUID, String>> {
    private final JSONParser jsonParser;
    private final List<UUID> uuids;
    
    public NameFetcher(final List<UUID> uuids) {
        this.jsonParser = new JSONParser();
        this.uuids = ImmutableList.copyOf(uuids);
    }
    
    public Map<UUID, String> call() throws Exception {
        final Map<UUID, String> uuidStringMap = new HashMap<UUID, String>();
        for (final UUID uuid : this.uuids) {
            final HttpURLConnection connection = (HttpURLConnection)new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "")).openConnection();
            final JSONObject response = (JSONObject)this.jsonParser.parse((Reader)new InputStreamReader(connection.getInputStream()));
            final String name = (String)response.get("name");
            if (name == null) {
                continue;
            }
            final String cause = (String)response.get("cause");
            final String errorMessage = (String)response.get("errorMessage");
            if (cause != null && cause.length() > 0) {
                throw new IllegalStateException(errorMessage);
            }
            uuidStringMap.put(uuid, name);
        }
        return uuidStringMap;
    }
}
