import java.util.*;
class Search{
    //Annealing Search:
    Plan Annealing(Problem problem, long experation){
        Plan current = problem.Empty(); Plan next = problem.Empty();
        int temperature = 10000; double energy;
        while(true){
            if(temperature == 0){return current;}
            next = NextSch(problem);
            energy = problem.Eval_Sch(next) - problem.Eval_Sch(current);
            if(energy > 0){ current = next;}
            temperature --;
        }
    }
    //Populate a new schedule from a problem
    private Plan NextSch(Problem problem){
        int row = 0; int col = 0;
        Plan next = problem.Empty();

        for(int i = 0; i < Problem.crs.size(); i++){
            Crs random = Problem.crs.get(i);
            row = (int)(Math.random() * random.time_slot.length);
            col = (int)(Math.random() * problem.rms.size());
            while(next.sch[col][row] != -1){
                row = (int)(Math.random()*random.time_slot.length);
                col = (int)(Math.random()*problem.rms.size());
            }
            next.sch[col][row] = i;
        }
        return next;
    }
    // Baseline Search:
    Plan Baseline(Problem problem, long experation) {
        Plan sol = problem.Empty();
        for (int i = 0; i < Problem.crs.size(); i++) {
            Crs c = Problem.crs.get(i);
            boolean sch = false;
            for (int j = 0; j < c.time_slot.length; j++) {
                if (sch){break;}
                if (c.time_slot[j] > 0) {
                    for (int k = 0; k < Problem.rms.size(); k++) {
                        if (sol.sch[k][j] < 0) {
                            sol.sch[k][j] = i;
                            sch = true; break;
                        }
                    }
                }
            }
        }
        return sol;
    }

    //Backtracking Search Methods to aid in search output:

    //course scheduled has a resolution
    private boolean Answer(Problem problem) {
        for (int i = 0; i < problem.crs.size(); i++) {
            Crs course = problem.crs.get(i);
            if(!course.sch){return false;}
        }
        return true;
    }

    //Heuristic
    private Crs Heuristic(ArrayList<Crs> list, Crs crs_1, Crs crs_2) {
        Crs course;
        if (crs_1 == null && crs_2 == null) return null;
        if (crs_1 == null) return crs_2;
        if (crs_2 == null) return crs_1;

        //Initialization
        int[] clash_1 = new int[crs_1.time_slot.length];
        int[] clash_2 = new int[crs_2.time_slot.length];
        int loc_1 = list.indexOf(crs_1);
        int loc_2 = list.indexOf(crs_2);
        int total_1 = 0, total_2 = 0;

        //Implementation
        for (int i = 0; i < list.size(); i++) {
            course = list.get(i);

            if ((list.indexOf(crs_1) != i) && (i != loc_2)){
                for (int j = 0; j < clash_1.length; j++){
                    if ((crs_1.time_slot[j] > 0) && (course.time_slot[j] > 0)){clash_1[j]++;}
                }
            }

            if ((list.indexOf(crs_2) != i) && (i != loc_1)){
                for (int j = 0; j < clash_2.length; j++){
                    if ((crs_2.time_slot[j] > 0) && (course.time_slot[j] > 0)){clash_2[j]++;}
                }
            }
        }//For Loop END

        for (int i = 0; i < crs_1.time_slot.length; i++){
            total_1 += clash_1[i]; total_2 += clash_2[i];
        }

        //largest constraints:
        if (total_2 < total_1) { return crs_1;}
        else{return crs_2;}
    }

    // Least Constraining:
    private int[] few_constraints(Crs current, ArrayList<Crs> list) {
        int[] conflict = new int[current.time_slot.length]; Crs course = null;
        for (int i = 0; i < list.size(); i++) {
            course = list.get(i);
            if (list.indexOf(current) != i) {
                for (int j = 0; j < conflict.length; j++) {
                    if ((course.time_slot[j] > 0) && (current.time_slot[j] > 0))
                        conflict[j]++;
                }
            }
        }
        return conflict;
    }

    // Least values left: search improvement
    private Crs Min_value(ArrayList<Crs> list) {
        //Init:
        Crs course = null; Crs least_num_of_crs = null;
        int least = Integer.MAX_VALUE; int value;

        //Implementation:
        for (int i = 0; i < list.size(); i++) {
            value = 0; course = list.get(i);
            for (int j = 0; j < course.time_slot.length; j++) {
                if (course.time_slot[j] > 0){value ++;}
            }
            if (least == value) {least_num_of_crs = Heuristic(list, course, least_num_of_crs);}
            if (least > value) {
                least = value;
                least_num_of_crs = course;
            }

        }
        return least_num_of_crs;
    }

    // Solve Recursively: Backtracking
    private Plan Recursive_solution(Problem problem, Plan solution, ArrayList<Crs> temp_crs) {
        int place_holder = 0;
        if (temp_crs.size() == 0){return solution;}

        // MRV heuristic
        Crs course = Min_value(temp_crs);

        if (course == null) {return solution;}
        temp_crs.remove(temp_crs.indexOf(course));

        // LSV heuristic
        int conflict[] = few_constraints(course, problem.crs);

        int pos = Integer.MAX_VALUE;

        for (int i = 0; i < conflict.length; i++) {
            if ((conflict[i] < pos) && (conflict[i] > 0)) {
                pos = conflict[i];
                place_holder = i;
            }
        }
        for (int i = 0; i < problem.rms.size(); i++) {
            if (solution.sch[i][place_holder] < 0) {
                if (course.enrolled <= problem.rms.get(i).cap) {
                    solution.sch[i][place_holder] = problem.crs.indexOf(course);
                    break;
                }
            }
        }
        course.sch = true;
        solution = Recursive_solution(problem, solution, temp_crs);
        if (!Answer(problem)) {
            course.time_slot[place_holder] = 0;
            temp_crs.add(course);
        }
        return solution;
    }

    //BackTracking Algorithm:
    Plan BackTracking(Problem problem, long experation){
        Plan solution = problem.Empty();
        ArrayList<Crs>temp_crs = new ArrayList<Crs>(problem.crs);
        return solution = Recursive_solution(problem, solution, temp_crs);
    }
}
