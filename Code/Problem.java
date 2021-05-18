import java.util.*;
class Problem{
    private static final int Slot = 10;
    private static final double X = 10;
    private static final double Y = 10;
    private static final double  Cost = 2.5d;
    private static ArrayList<Place> pls; static ArrayList<Rm> rms; static ArrayList<Crs> crs;
    private static Random ran;

    Problem(long Seed){
        if(Seed < 0){ran = new Random();}
        else{ran = new Random(Seed);}
        pls = new ArrayList<Place>(); rms = new ArrayList<Rm>(); crs = new ArrayList<Crs>();
    }

    //Method will establish buildings, rooms, and courses for the problem
    static void createInstance(int Building, int Rooms, int Course){
        for(int i=0; i < Building; i++){
            Place temp = new Place();
            temp.x_axis = ran.nextDouble() * X;
            temp.y_axis = ran.nextDouble() * Y;
            pls.add(temp);
        }

        for(int i=0; i < Rooms; i++){
            Rm temp = new Rm();
            temp.p = pls.get((int)(ran.nextDouble() * Building));
            temp.cap = ((int)(ran.nextDouble() * 70)) + 30;
            rms.add(temp);
        }

        for(int i=0; i < Course; i++){
            Crs temp = new Crs();
            temp.enrolled = ((int) (ran.nextDouble() * 70)) + 30;
            temp.location = pls.get((int)((ran.nextDouble() * Building)));
            temp.value = ran.nextDouble() * 100;
            temp.time_slot = new int[Slot];

            for(int j=0; j < Slot; j++){
                if(ran.nextDouble() < .3d){temp.time_slot[j] = 0;}
                else{temp.time_slot[j] = (int)(ran.nextDouble() * 10);}
            }
            crs.add(temp);
        }
    }

    //Empty Situation:
    Plan Empty(){
        Plan temp = new Plan(rms.size(), Slot);
        for(int i=0; i < rms.size(); i++){
            for(int j=0; j < Slot; j++){
                temp.sch[i][j] = -1;
            }
        }
        return temp;
    }

    // Check to see if classes get issued without conflict
    Double Eval_Sch(Plan solution){
        int [][] temp = solution.sch;
        //Pre-caution
        if(temp[0].length != Slot || temp.length != rms.size()){ System.out.println("Invalid");
        return Double.NEGATIVE_INFINITY;}

        //Begin checking for conflicts:
        int [] given = new int[crs.size()];
        for (int[] ints : temp) {
            for (int j = 0; j < temp[0].length; j++) {
                if (ints[j] > crs.size() || ints[j] < 0) {continue;}
                if (given[ints[j]] > 0) {return Double.NEGATIVE_INFINITY;}
                given[ints[j]]++;
            }
        }

        double val = 0d;
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[0].length; j++) {
                if (temp[i][j] < 0 || temp[i][j] > crs.size()){continue;}
                Crs c = crs.get(temp[i][j]); Rm r = rms.get(i);
                if (c.time_slot[j] <= 0){continue;}
                if (c.enrolled > r.cap){continue;}
                val += c.value; val += c.time_slot[j];

                //calculate Cost
                Place b1 = r.p; Place b2 = c.location; double distance;
                double x = (b1.x_axis - b2.x_axis) * (b1.x_axis - b2.x_axis);
                double y = (b1.y_axis - b2.y_axis) * (b1.y_axis - b2.y_axis);
                distance = Math.sqrt(x + y);
                val -= Cost * distance;
            }
        }
        return val;
    }
}
