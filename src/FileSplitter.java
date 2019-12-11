import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FileSplitter {
    public String masterTagListPath = "";
    public List<String> masterList;
    public String outputDirPath = "";
    public FileSplitter(String masterTagListPath,String outputDirPath) {
        this.masterTagListPath = masterTagListPath;
        this.outputDirPath = outputDirPath;
        try {
            Files.createDirectory(Paths.get(this.outputDirPath));
            this.masterList = Files.readAllLines(Paths.get(masterTagListPath), StandardCharsets.ISO_8859_1);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FileSplitter splitter = new FileSplitter("c:/Users/Odium Dei/Desktop/IntermediateFicData/taglist20.txt",
                "c:/Users/Odium Dei/Desktop/FlatFicOutput/");
        try {
            splitter.splitAllFiles("c:/Users/Odium Dei/Desktop/FlatFicData/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class SplitConsumer implements Consumer<Path> {
        @Override
        public void accept(Path path) {
            try {
                if(!Files.isDirectory(path)) {
                    splitFile(path);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void splitAllFiles(String rootpath) throws IOException {
        Stream<Path> pathStream = Files.walk(Paths.get(rootpath));
        pathStream.forEach(new SplitConsumer());
    }

    public void splitFile(Path path) throws IOException {
        List<String> file = Files.readAllLines(path,StandardCharsets.ISO_8859_1);
        boolean notDone = true;
        int lineSplit = 0;
        for (int i = 0; i < file.size() && notDone; i++) {
            if(file.get(i).matches("Hits:")) {
                lineSplit = i + 2;
                notDone = false;
                break;
            }
        }
        List<String> outputList = new ArrayList<>();
        Files.write(Paths.get(outputDirPath+path.getFileName()),file.subList(lineSplit, file.size()), StandardOpenOption.CREATE);
        file = Main.tagContextTransformer(file.subList(0, lineSplit));
        for (int i = 0; i < file.size(); i++) {
            if(this.masterList.contains(file.get(i))) {
                outputList.add(file.get(i));
            }
        }
        String outFilePath = path.getFileName().toString().replaceAll("\\.txt","")+"_tags.txt";
        Files.write(Paths.get(outputDirPath+outFilePath),outputList);
    }
}
