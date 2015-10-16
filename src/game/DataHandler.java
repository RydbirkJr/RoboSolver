package game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Mapped {
    public String gameID;
    public int moves;

    public Mapped(String gameID, int moves){
        this.gameID = gameID;
        this.moves = moves;
    }
}

class PercentageOptimal{
    public int optimalMoves;
    public boolean isOptimal;

    PercentageOptimal(int optimalMoves, boolean isOptimal){
        this.optimalMoves = optimalMoves;
        this.isOptimal = isOptimal;
    }

    public int getOptimalMoves(){
        return optimalMoves;
    }
}

class Mapped2{
    public int moves;
    public long percentage;

    public Mapped2(int moves, long percentage){
        this.moves = moves;
        this.percentage = percentage;
    }
}

/**
 * Created by Anders on 16/10/15.
 */
public class DataHandler {

    private String _directive;

    public DataHandler(String directive){
        _directive = directive;
    }

    public void generateCompletionData(){
        String[] solvers = new String[]{Solver.GRAPHv1, Solver.GRAPHv2, Solver.NAIVE, Solver.STATELESS};

        for(int i = 0; i < solvers.length; i++){
            List<CsvData> data = readFile(solvers[i]);
            List<CsvData> iddfs = readFile(Solver.IDDFS);

            Stream<Mapped2> mapped = data.stream()
                    .flatMap(v1 -> iddfs
                            .stream()
                            .map(p -> new Mapped(p.gameID, p.moves))
                                    .filter(v2 -> v1.gameID.equals(v2.gameID))
                                    .map(v2 -> new PercentageOptimal(v2.moves, v1.isOptimal)))
                    .collect(Collectors.groupingBy(PercentageOptimal::getOptimalMoves))
                    .entrySet()
                    .stream()
                    .map(i1 -> new Mapped2(i1.getKey(),
                                    (i1.getValue()
                                            .stream()
                                            .filter(i2 -> i2.isOptimal)
                                            .count()
                                            /
                                            ((long) i1.getValue()
                                                    .size()))
                            )
                    );

            Map<Integer, String> res = new HashMap<>();

            mapped.forEach(m -> res.put(m.moves, (m.percentage * 100) + ""));

            System.out.println(String.format("%s before write.", solvers[i]));
            writeToFile(solvers[i], "percentage", res);
            System.out.println(String.format("%s after write.", solvers[i]));
        }
    }

    public void generateAverageTimeData(){
        String[] solvers = new String[]{Solver.GRAPHv1, Solver.GRAPHv2, Solver.IDDFS, Solver.NAIVE, Solver.STATELESS};

        for(int i = 0; i < solvers.length; i++){
            List<CsvData> data = readFile(solvers[i]);

            Map<Integer, Double> avg =  data.stream().collect(Collectors.groupingBy(CsvData::getMoves, Collectors.averagingLong(s -> s.time)));

            Map<Integer, String> mapped = new HashMap<>();
            avg.forEach((k,v) -> mapped.put(k, ( (Integer) v.intValue()).toString()));
            writeToFile(solvers[i], "avg", mapped);
        }
    }



    public void generateDistributionData(){
        List<CsvData> data = readFile(Solver.IDDFS);

        Map<Integer,Long> dist = data.stream().collect(Collectors.groupingBy(CsvData::getMoves, Collectors.counting()));

        Map<Integer, String> mapped = new HashMap<>();

        dist.forEach((k, v) -> mapped.put(k, v.toString()));

        writeToFile(Solver.IDDFS, "dist", mapped);
    }

    private List<CsvData> readFile(String filename){
        final String delimiter = ";";
        final String extension = ".csv";
        String line = "";
        BufferedReader in = null;

        ArrayList<CsvData> list = new ArrayList<CsvData>();

        try{
            in = new BufferedReader(new FileReader(_directive + "/" + filename + extension));

            while((line = in.readLine()) != null){
                String[] elements = line.split(delimiter);
                list.add(new CsvData(String.format("%s;%s;%s;%s,%s", elements[0], elements[1],elements[2],elements[3],elements[4]),
                        Integer.parseInt(elements[5]),
                        Long.parseLong(elements[6]),
                        Boolean.parseBoolean(elements[7]),
                        Boolean.parseBoolean(elements[8])
                ));
            }
            in.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    private void  writeToFile(String solver, String type, Map<Integer,String> map){
        String fileName = String.format("%s_%s.csv",type, solver);

        FileWriter out;
        try {
            out = new FileWriter(_directive + "/" + fileName, true);

            for(Map.Entry<Integer, String> entry : map.entrySet()){
                out.write(String.format("%d;%s\n",entry.getKey(),entry.getValue()));
            }

            out.close();

        } catch(Exception e){
            e.printStackTrace();
        }

    }
}
