import java.util.ArrayList;
import Vector.Vector;
public class ShortTermMem {
    ArrayList<ArrayList<Vector>> scans = new ArrayList<>();
    ArrayList<Vector> scanPositions = new ArrayList<>();

    ArrayList<Long> scanTimes = new ArrayList<>();
    private int size = 0;

    public void addScan(Vector scanPosition, ArrayList<Vector> scan){
        size += scan.size();
        scans.add(scan);
        scanPositions.add(scanPosition);
        scanTimes.add(System.currentTimeMillis());
        purgeScans();
    }

    public ArrayList<Vector> getPoints(){
        ArrayList<Vector> points = new ArrayList<>();
        for(ArrayList<Vector> pointList : this.scans){
            points.addAll(pointList);
        }
        return points;
    }

    public void remove(Vector point){
        for(ArrayList<Vector> pointList : this.scans){
            int listSize = pointList.size();
            pointList.remove(point);
            if(listSize - pointList.size() != 0){
                size--;
                break;
            }
        }
    }

    private void purgeScans(){
        long currentTime = System.currentTimeMillis();
        int i = scanTimes.size();

        // loop through the list backwards and remove all scans that are over second old
        // we loop backwards to avoid removal conflicts
        while(i > 0){
            i--;
            long dt = currentTime - scanTimes.get(i);
            if(dt < 1000){
                continue;
            }
            size -= scans.get(i).size();
            scanTimes.remove(i);
            scanPositions.remove(i);
            scans.remove(i);
        }
    }

    public int size(){
        return this.size;
    }
}
