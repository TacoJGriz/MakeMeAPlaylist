package com.example.makemeaplaylist;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

import java.util.ArrayList;
public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    public NetworkUtils() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ArrayList<String> runBasic(String askFor, String param, String KEY) {

        ArrayList<String> output = new ArrayList<>();
        ArrayList<String> stopSeq = new ArrayList<>();
        stopSeq.add("11");

        OpenAiService service = new OpenAiService(KEY);
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt("Make me a playlist with 10 tracks given a " + askFor + "\n" + askFor + ": " + param + "\nPlaylist:")
                .model("gpt-4o-mini")
                .temperature(0.7)
                .maxTokens(270)
                .topP(1.0)
                .frequencyPenalty(1.0)
                .presencePenalty(1.0)
                .stop(stopSeq)
                .echo(true)
                .build();
        service.createCompletion(completionRequest).getChoices().forEach(ln -> output.add(ln.toString()));

        return output;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ArrayList<String> runGivenPlaylist(ArrayList<Song> playlist, int len, String KEY) {
        ArrayList<String> output = new ArrayList<>();

        ArrayList<String> stopSeq = new ArrayList<>();
        stopSeq.add("" + (len + 1));
        StringBuilder prompt = new StringBuilder("Make me a playlist with " + len + " tracks\nPlaylist:");

        int i = 1;
        for (Song song : playlist) {
            prompt.append("\n").append(i).append(". \"").append(song.getName()).append("\" by ").append(song.getArtist());
            i++;
        }

        OpenAiService service = new OpenAiService(KEY);
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt.toString())
                .model("text-curie-001")
                .temperature(0.7)
                .maxTokens(400)
                .topP(1.0)
                .frequencyPenalty(1.0)
                .presencePenalty(1.0)
                .stop(stopSeq)
                .echo(true)
                .build();
        service.createCompletion(completionRequest).getChoices().forEach(ln -> output.add(ln.toString()));

        return output;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ArrayList<Song> processOutput(String output, int length) {
        String newString = output.substring(output.indexOf("Playlist:") + 10, output.indexOf(", index="));
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> artist = new ArrayList<>();

        String tempString;
        String tempArtist;
        String tempName;

        boolean stop = false;

        int startI;
        int oldStartI = 0;

        for (int i = 1; i < length + 1; i++) {
            startI = newString.indexOf("" + i, oldStartI) + ("" + i).length();

            if (i < length && newString.indexOf("" + (i + 1), startI) > oldStartI) {
                tempString = newString.substring(startI, newString.indexOf((i + 1) + "", startI));
            } else {
                tempString = newString.substring(startI);
                stop = true;
            }

            if (tempString.charAt(0) == '.') {
                tempString = tempString.substring(1);
            }

            if (tempString.charAt(tempString.length()-1) == '0') {
                tempString = tempString.substring(0,length-1);
            }

            tempString = tempString.trim();

            if (tempString.contains(" –")) {
                String sub1 = tempString.substring(tempString.indexOf(" –") + 2);
                if (tempString.charAt(0) == '\"') {
                    tempName = tempString.substring(0, tempString.indexOf(" –"));
                    tempArtist = sub1;
                } else {
                    tempArtist = tempString.substring(0, tempString.indexOf(" –"));
                    tempName = sub1;
                }
            } else if (tempString.contains(" -")) {
                String sub2 = tempString.substring(tempString.indexOf(" -") + 2);
                if (tempString.charAt(0) == '\"') {
                    tempName = tempString.substring(0, tempString.indexOf(" -"));
                    tempArtist = sub2;
                } else {
                    tempArtist = tempString.substring(0, tempString.indexOf(" -"));
                    tempName = sub2;
                }
            } else if (tempString.toLowerCase().contains("by ")) {
                tempArtist = tempString.substring(tempString.toLowerCase().indexOf("by ") + 3);
                tempName = tempString.substring(0, tempString.toLowerCase().indexOf("by "));
            } else if (tempString.toLowerCase().contains(": ")) {
                tempName = tempString.substring(tempString.toLowerCase().indexOf(": ") + 2);
                tempArtist = tempString.substring(0, tempString.toLowerCase().indexOf(": "));
            } else {
                tempName = tempString;
                tempArtist = "";
            }

            tempName = tempName.trim();

            if (tempName.charAt(0) == '"') {
                tempName = tempName.substring(1);
                if (tempName.endsWith("\"")) {
                    tempName = tempName.substring(0, tempName.length() - 1);
                }
            }

            names.add(tempName);
            artist.add(tempArtist);

            oldStartI = startI;

            if(stop) break;
        }

        ArrayList<Song> songs = new ArrayList<>();

        for (int i = 0; i < names.size(); i++) {
            songs.add(new Song(artist.get(i), names.get(i)));
        }

        songs.forEach(song -> Log.d(LOG_TAG, song.toString()));
        return songs;
    }
}