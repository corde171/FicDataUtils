import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class MasterTagList {
    private Map<String, Integer> tagMap;
    private int minFreq = 0;
    private ArrayList<String> outputList;

    public void setTagMap() {
        throw new UnsupportedOperationException("You can't set the Tag Map to a new value.");
    }

    public MasterTagList() {
        tagMap = new HashMap<>();
        outputList = new ArrayList<>();
    }

    public Map<String, Integer> getTagMap() {
        return this.tagMap;
    }

    private class BiTagger implements BiConsumer<String, Integer> {
        /**
         * Performs this operation on the given arguments.
         *
         * @param s       the first input argument
         * @param integer the second input argument
         */
        @Override
        public void accept(String s, Integer integer) {
            if(integer >= minFreq) {
                outputList.add(s);
            }
        }
    }

    public void writeTagMapToFile(String writeToFile, int minFreq) throws IOException
    {
        writeToFile = writeToFile.replaceAll(".txt", "")+minFreq+".txt";
        this.minFreq = minFreq;
        this.tagMap.forEach(new BiTagger());
        this.outputList.add(0,"Minimum Frequency: " + minFreq + "\r\nTotal Tags: " + this.outputList.size());
        Files.write(Paths.get(writeToFile),this.outputList);
    }

    public void insertTag(String tag) {
        if(tagMap.containsKey(tag)) {
            tagMap.put(tag, tagMap.get(tag) + 1);
        }
        else {
            tagMap.put(tag, 1);
        }
    }

    public void insertListOfTags(List<String> tags) {
        if(tags == null) {
            return;
        }
        for (int i = 0; i < tags.size(); i++) {
            insertTag(tags.get(i));
        }
    }
}
