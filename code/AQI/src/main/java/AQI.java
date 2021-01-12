import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.*;

public class AQI {

    public static class IntArrayWritable extends ArrayWritable {
        public IntArrayWritable(){
            super(IntWritable.class);
        }

        public IntArrayWritable(IntWritable[] values) {
            super(IntWritable.class, values);
        }

        @Override
        public IntWritable[] get() {
            return (IntWritable[]) super.get();
        }

        @Override
        public String toString() {
            IntWritable[] values = get();
            String strings = "";
            for (IntWritable value : values) {
                strings = strings + " " + value.toString();
            }
            return strings;
        }
    }

    public static class AQIMapper extends Mapper<Object, Text, Text, IntWritable> {
        private final Text city = new Text();
        private final IntWritable aqi = new IntWritable();
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            if(key.toString().equals("0")){
                return ;
            }
            String line = value.toString();
            String[] arr = line.split(",");
            IntWritable[] tmp = new IntWritable[2];
            IntArrayWritable output = new IntArrayWritable(tmp);
            if(arr[arr.length-5].equals("2019") && arr[arr.length-4].equals("2")){
                city.set(String.format("%s,%s", arr[arr.length-1], arr[arr.length-3]));
                aqi.set(Integer.parseInt(arr[11]));
                context.write(city, aqi);
            }
        }
    }



    public static class AQIReducer extends Reducer<Text, IntWritable, Text, IntArrayWritable> {
        Map<String, ArrayList<Integer>> maps = new HashMap<String, ArrayList<Integer>>();
        public void reduce(Text city, Iterable<IntWritable> aqis, Context context) throws IOException, InterruptedException {
            int num=0;
            int sum=0;
            int avg=0;
            ArrayList<Integer> integers = new ArrayList<Integer>();
            for(IntWritable aqi:aqis){
                sum += aqi.get();
                num++;
            }
            avg=(int)(sum/num);
            integers.add(avg);
            String[] split = city.toString().split(",");
            if (maps.get(split[0])!=null){
                maps.get(split[0]).add(avg);
            }else{
                maps.put(split[0], integers);
            }


        }

        protected void cleanup(Reducer<Text, IntWritable, Text, IntArrayWritable>.Context context) throws IOException, InterruptedException {
            Set<Map.Entry<String, ArrayList<Integer>>> entries = maps.entrySet();
            for(Map.Entry<String, ArrayList<Integer>> entry:entries){
                int[] list = new int[7];
                System.out.println(entry.getValue().size());
                for(Integer num:entry.getValue()){
                    list[num]=list[num]+1;
                }
                IntWritable[] tmp = new IntWritable[7];
                IntArrayWritable output = new IntArrayWritable(tmp);
                tmp[0]= new IntWritable(list[0]);
                tmp[1]= new IntWritable(list[1]);
                tmp[2]= new IntWritable(list[2]);
                tmp[3]= new IntWritable(list[3]);
                tmp[4]= new IntWritable(list[4]);
                tmp[5]= new IntWritable(list[5]);
                tmp[6]= new IntWritable(list[6]);
                context.write(new Text(entry.getKey()), new IntArrayWritable(output.get()));
            }
        }

    }



    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "average");
        job.setJarByClass(AQI.class);
        job.setMapperClass(AQIMapper.class);
//        job.setCombinerClass(AQIReducer.class);
        job.setReducerClass(AQIReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntArrayWritable.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);


        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
