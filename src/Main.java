import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Main {
    private MasterTagList list;
    private static String rootpath = "c:/Users/Odium Dei/Desktop/IntermediateFicData/";
    public static void main(String[] args) {
        Main main = new Main();
        try {
            main.TagGetter(rootpath);
            main.list.writeTagMapToFile(rootpath + "taglist.txt", 20);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Main() {
        list = new MasterTagList();
    }

    private class TagConsumer implements Consumer<Path> {
        /**
         * Performs this operation on the given argument.
         *
         * @param path the input argument
         */
        @Override
        public void accept(Path path) {
            try {
                Main.this.list.insertListOfTags(tagAgg(path));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void TagGetter(String rootpath) throws IOException, SecurityException {
        Stream<Path> pathStream = Files.walk(Paths.get(rootpath));
        pathStream.forEach(new TagConsumer());
    }

    private List<String> tagAgg(Path path) throws IOException {
        if(Files.isDirectory(path)) {
            return null;
        }
        List<String> stringList = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
        return tagContextTransformer(stringList);
    }

    public static List<String> tagContextTransformer(List<String> stringList) {
        String prefix = "";
        List<String> tagsList = new ArrayList<>();
        for (int i = 0; i < stringList.size(); i++) {
            if(stringList.get(i).matches("<Language:>")) {
                break;
            }
            if(stringList.get(i).matches("<.*:>")) {
                prefix = stringList.get(i).replaceAll("<","");
                prefix = prefix.replaceAll(":>","");
                prefix = prefix + " - ";
            }
            else {
                tagsList.add(prefix + stringList.get(i));
            }
        }
        return tagsList;
    }
}
