import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class TagsToVec {
    private String basePath = "";
    private List<String> masterTagList;
    private Map<String, Integer> masterMap;

    public static void main(String[] args) {
        TagsToVec tagsToVec = new TagsToVec("","");
        Stream<Path> pathStream;
        try {
            pathStream = Files.walk(Paths.get(tagsToVec.basePath));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public class VecConsumer implements Consumer<Path> {
        @Override
        public void accept(Path path) {
            if(!Files.isDirectory(path)) {
                try {
                    TagsToVec.this.writeVec(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public TagsToVec(String basePath, String masterListPath) {
        this.basePath = basePath;
        try {
            this.masterTagList = Files.readAllLines(Paths.get(masterListPath));
            this.masterMap = new HashMap<>();
            for (int i = 0; i < masterTagList.size(); i++) {
                masterMap.put(masterTagList.get(i),i);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Integer[] matchedTags(List<String> tagsToCheck) {
        Integer[] vec = new Integer[masterMap.size()];
        Arrays.fill(vec, 0);
        for (int i = 0; i < tagsToCheck.size(); i++) {
            if(this.masterTagList.contains(tagsToCheck.get(i))) {
                vec[(masterMap.get(tagsToCheck.get(i)))] = 1;
            }
        }
        return vec;
    }



    private void writeVec(Path path) throws IOException {
        List<String> fileTags = Files.readAllLines(path);
        Integer[] vec = matchedTags(fileTags);
        String toWrite = "";
        for (int i = 0; i < vec.length; i++) {
            toWrite = toWrite.concat(vec[i] + ",");
        }
        toWrite = toWrite.substring(0,toWrite.length()-1);

        Files.write(path, toWrite.getBytes(StandardCharsets.ISO_8859_1), StandardOpenOption.APPEND,StandardOpenOption.WRITE);
    }
}
