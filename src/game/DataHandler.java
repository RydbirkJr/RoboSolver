package game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Mapped {
    public String gameID;
    public int moves;

    public Mapped(int countOptimal, int countTotal){

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

            Map<Integer, List<CsvData>> mapped = data.stream()
                    .collect(Collectors.groupingBy((CsvData d) -> d.optimalMoves));

            Map<Integer, String> result = new HashMap<>();

            mapped.forEach((k,v) -> {
                double total = v.size();
                double optimal = v.stream().filter(l -> l.isOptimal).count();
                double percentage = optimal / total * 100.0;

                result.put(k, percentage + "");
            });

            writeToFile(solvers[i], "percentage", result);
        }
    }

    public void generateAverageTimeDataBySolution(){
        String[] solvers = new String[]{Solver.GRAPHv1, Solver.GRAPHv2, Solver.IDDFS, Solver.NAIVE, Solver.STATELESS};

        for(int i = 0; i < solvers.length; i++){
            List<CsvData> data = readFile(solvers[i]);

            Map<Integer, Double> avg =  data.stream().collect(Collectors.groupingBy(d -> d.moves, Collectors.averagingLong(s -> s.time)));

            Map<Integer, String> mapped = new HashMap<>();
            avg.forEach((k,v) -> mapped.put(k, ( (Integer) v.intValue()).toString()));
            writeToFile(solvers[i], "avg_solution", mapped);
        }
    }

    public void generateAverageTimeDataByOptimal(){
        String[] solvers = new String[]{Solver.GRAPHv1, Solver.GRAPHv2, Solver.IDDFS, Solver.NAIVE, Solver.STATELESS};

        for(int i = 0; i < solvers.length; i++){
            List<CsvData> data = readFile(solvers[i]);

            Map<Integer, Double> avg =  data.stream().collect(Collectors.groupingBy(d -> d.optimalMoves, Collectors.averagingLong(s -> s.time)));

            Map<Integer, String> mapped = new HashMap<>();
            avg.forEach((k,v) -> mapped.put(k, ( (Integer) v.intValue()).toString()));
            writeToFile(solvers[i], "avg_optimal", mapped);
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
                        Integer.parseInt(elements[7]),
                        Boolean.parseBoolean(elements[8]),
                        Boolean.parseBoolean(elements[9])
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
