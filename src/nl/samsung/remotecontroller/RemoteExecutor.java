package nl.samsung.remotecontroller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonObject;

public class RemoteExecutor {

	public static void main(String[] args) {
		try {
				
			// open websocket for communication
			final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(
					new URI("ws://192.168.1.1:8001/api/v2/channels/samsung.remote.control"));
			// add listener to send message
			clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
				public void handleMessage(String message) {
					System.out.println(message);
				}
			});

			//Key list for input
			List<String> keyList = new ArrayList();

			for (Keycode keys : Keycode.values()) {
				System.out.println(keys);
			}
			System.out.println("Use Above keys from this reference");
			System.out.println("Please insert the keys with space \n");
			System.out.println("To exit input use either KEY_ENTER or KEY_EXIT as last key"+"\n");
			
			//Scanning key list from user input
			Scanner userSelect = new Scanner(System.in);			
			String input = userSelect.nextLine();
			while (!input.equals("KEY_ENTER") || !input.equals("KEY_EXIT")) {
				keyList.add(input);
				input = userSelect.nextLine();
			}

			Iterator<String> keyIterator = keyList.iterator();

			final int KEY_DELAY = 1;

			//Sending message via websocket for each key
			while (keyIterator.hasNext()) {

				JsonObject sendKeyCommand = Json.createObjectBuilder().add("method", "ms.remote.control")
						.add("params",
								Json.createObjectBuilder().add("Cmd", "Click").add("DataOfCmd", keyIterator.next())
										.add("Option", "false").add("TypeOfRemote", "SendRemoteKey").build())
						.build();
				clientEndPoint.sendMessage(sendKeyCommand.toString(), KEY_DELAY);
				
				/*Please use below command if above command doesn't work*/
				// clientEndPoint.sendMessage("{'method':'ms.remote.control','params':{'Cmd':'Click',
				// 'DataOfCmd': keyIterator.next(), 'Option':'false',
				// 'TypeOfRemote':'SendRemoteKey'}}");

								
				System.out.println("Message sent via websocket with key " + keyIterator.next());
			}


			// wait 5 seconds for messages from websocket
			Thread.sleep(5000);

		} catch (InterruptedException ex) {
			System.err.println("InterruptedException exception: " + ex.getMessage());

		} catch (URISyntaxException ex) {
			System.err.println("URISyntaxException exception: " + ex.getMessage());
		}

	}

}
