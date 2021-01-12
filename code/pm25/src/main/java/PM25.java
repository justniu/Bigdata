import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.codehaus.jackson.map.ser.std.CollectionSerializer;

import java.io.IOException;
import java.util.*;

public class PM25 {
    public static class PMMapper extends Mapper<Object, Text, Text, FloatWritable>{
        private Text city = new Text();
        private final static FloatWritable pm25 = new FloatWritable();
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            if(key.toString().equals("0")){
                return ;
            }
            String line = value.toString();
            String[] arr = line.split(",");
            city.set(arr[arr.length-1]);
            pm25.set(Float.parseFloat(arr[3]));
            context.write(city, pm25);
        }
    }

    public static class PMReduce extends Reducer<Text, FloatWritable, Text, FloatWritable>{
        Map<String, Float> map = new HashMap<String, Float>();
        public void reduce(Text city, Iterable<FloatWritable> pm25s, Context context) throws IOException, InterruptedException {
            int num = 0;
            float sum = 0;
            float avg = 0;
            for(FloatWritable pm25:pm25s){
                float v = pm25.get();
                sum += v;
                num++;
            }
            avg = sum/num;
            map.put(city.toString(), avg);
        }

        protected void cleanup(Reducer<Text, FloatWritable, Text, FloatWritable>.Context context) throws IOException, InterruptedException {
            List<Map.Entry<String, Float>> list = new LinkedList<Map.Entry<String, Float>>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
                public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                    return (int)(o1.getValue() - o2.getValue());
                }
            });
            context.write(new Text(list.get(0).getKey()), new FloatWritable(list.get(0).getValue()));
            context.write(new Text(list.get(list.size()-1).getKey()), new FloatWritable(list.get(list.size()-1).getValue()));
        }
    }



    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "average");
        job.setJarByClass(PM25.class);
        job.setMapperClass(PMMapper.class);
        job.setCombinerClass(PMReduce.class);
        job.setReducerClass(PMReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FloatWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
