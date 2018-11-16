package net.runelite.client.plugins.grouptileindicators;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import org.json.JSONArray;

import javax.inject.Inject;
import java.net.URI;

public class GroupTileIndicatorsSocket {

     public Socket socket;


     @Inject
     private Client client;

    public GroupTileIndicatorsSocket() {


        socket = IO.socket(URI.create("https://lit-escarpment-92818.herokuapp.com"));

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Connected.");
            }
        });

        socket.on("tiles", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray array = (JSONArray) args[0];
                for(int i = 0; i < array.length(); i++){
                    try {
                        String q = array.getString(i);
                        System.out.println(q);
                        String[] coordString = q.split(":");
                        int x = Integer.valueOf(coordString[0]);
                        int y = Integer.valueOf(coordString[1]);
                        int z = Integer.valueOf(coordString[2]);

                        WorldPoint point = new WorldPoint(x,y,z);

                        GroupTileIndicatorsOverlay.tiles.add(point);
                    }catch(Exception e){


                    }
                }
                System.out.println("ON 'tiles': " + array);
            }
        });

        socket.on("tileAdded", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String tile =  (String) args[0];
                String[] coordString = tile.split(":");
                int x = Integer.valueOf(coordString[0]);
                int y = Integer.valueOf(coordString[1]);
                int z = Integer.valueOf(coordString[2]);

                WorldPoint point = new WorldPoint(x,y,z);
                if(!GroupTileIndicatorsOverlay.tiles.stream().anyMatch(t -> t.getX() == point.getX() && t.getY() == point.getY()
                    && t.getPlane() == point.getPlane())){
                    GroupTileIndicatorsOverlay.tiles.add(point);
                }
            }
        });

        socket.on("tags", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray array = (JSONArray) args[0];
                for(int i = 0; i < array.length(); i++){
                    try {
                        int q = array.getInt(i);
                      if(!GroupTileIndicatorsOverlay.npcs.contains(q)){
                          GroupTileIndicatorsOverlay.npcs.add(q);
                      }

                    }catch(Exception e){


                    }
                }
                System.out.println("ON 'tag': " + array);
            }
        });


        socket.on("NPCTagged", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Integer index =  (Integer) args[0];



                if(!GroupTileIndicatorsOverlay.npcs.contains(index)){
                    GroupTileIndicatorsOverlay.npcs.add(index);
                }
            }
        });

        socket.on("tileRemoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String tile =  (String) args[0];
                String[] coordString = tile.split(":");
                int x = Integer.valueOf(coordString[0]);
                int y = Integer.valueOf(coordString[1]);
                int z = Integer.valueOf(coordString[2]);

                WorldPoint point = new WorldPoint(x,y,z);

                    GroupTileIndicatorsOverlay.tiles.removeIf(t -> t.getX() == point.getX() && t.getY() == point.getY()
                            && t.getPlane() == point.getPlane());

            }
        });

        socket.on("tagRemoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Integer index =  (Integer) args[0];

                GroupTileIndicatorsOverlay.npcs.remove(index);

            }
        });

        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Disconnected.");
            }
        });

        socket.connect();



//        socket.emit("newTile", "4:5:6");

//        socket.emit("newTile", "4:5:6");
    }


    /*
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("WebSocket connected");
    }

    @Override
    public void onMessage(String s) {


    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }
    */
}
