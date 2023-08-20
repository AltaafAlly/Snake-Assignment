import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import za.ac.wits.snake.DevelopmentAgent;

public class MyAgent extends DevelopmentAgent {

    public static void main(String args[]) {
        MyAgent agent = new MyAgent();
        MyAgent.start(agent, args);
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String initString = br.readLine();
            String[] temp = initString.split(" ");
            int nSnakes = Integer.parseInt(temp[0]);

            while (true) {
                String line = br.readLine();
                if (line.contains("Game Over")) {
                    break;
                }
                //get board size
                int boardX = Integer.parseInt(temp[1]);
                int boardY = Integer.parseInt(temp[2]);
                int[][] BoardSize = new int[boardX][boardY];
                for (int i = 0; i < boardX; i++) {
                    for (int j = 0; j < boardY; j++) {
                        BoardSize[j][i] = -1;
                    }
                }
                String apple1 = line;


                //get apple coordinates
                String[] AppleCoor = apple1.split(" ");
                int appleX = Integer.parseInt(AppleCoor[0]);
                int appleY = Integer.parseInt(AppleCoor[1]);
                BoardSize[appleX][appleY] = -2;


                //Draw Zombies
                String[] ZombieHeads = {" ", " ", " ", " ", " ", " "};
                for (int i = 0; i < 6; i++) {
                    String zombieLine = br.readLine();
                    drawSnake(BoardSize, 9, zombieLine);
                    //add to zombie head
                    String vals[] = zombieLine.split(" ");
                    String ZombieHead = vals[0];
                    ZombieHeads[i] = ZombieHead;
                }

                //for my snake
                int mySnakeNum = Integer.parseInt(br.readLine());
                int direction = 0;
                String Head = "0,0";
                int headX = 0;
                int headY = 0;
                int tailX = 0;
                int tailY = 0;
                int AfterHeadX = 0;
                int AfterHeadY = 0;
                int move = 0;
                int length = 0;
                String[] EnemyHead = {" ", " ", " "};
                int NoOfEnemy = 0;
                String[] splitAltaafsnake = {" ", " ", " ", " ", " ", " "};

                int Snakelength = 0;
                for (int i = 0; i < nSnakes; i++) {
                    String snakeLine = br.readLine();

                    if (i == mySnakeNum) {

                        splitAltaafsnake = snakeLine.split(" ");
                        Head = splitAltaafsnake[3];

                        String Length = splitAltaafsnake[1];
                        String valAfterHead = splitAltaafsnake[4];

                        //now we get x and y
                        String[] HeadXY = Head.split(",");
                        String[] TailXY = splitAltaafsnake[splitAltaafsnake.length - 1].split(",");
                        String[] AfterHeadXY = valAfterHead.split(",");
                        String slength = splitAltaafsnake[1];
                        length = Integer.parseInt(slength);
                        headX = Integer.parseInt(HeadXY[0]);
                        headY = Integer.parseInt(HeadXY[1]);
                        tailX = Integer.parseInt(TailXY[0]);
                        tailY = Integer.parseInt(TailXY[1]);
                        AfterHeadX = Integer.parseInt(AfterHeadXY[0]);
                        AfterHeadY = Integer.parseInt(AfterHeadXY[1]);
                        if (headX == AfterHeadX) {
                            if (headY < AfterHeadY) {
                                direction = 0; //Snake goes UP
                            } else {
                                direction = 1;//Snake goes down
                            }
                        } else if (headY == AfterHeadY) {
                            if (headX < AfterHeadX) {
                                direction = 2; //Snake goes Left
                            } else {
                                direction = 3;//snake goes right
                            }
                        }

                    }

                    //do stuff with other snakes
                    String[] enemyLine = snakeLine.split(" ", 4);
                    String EnemyInfo = "";
                    if ((enemyLine.length == 4) && (enemyLine[0].equals("alive"))) {
                        EnemyInfo = enemyLine[3];
                        drawSnake(BoardSize, i, EnemyInfo);
                        if (i != mySnakeNum) {
                            String[] enHead = EnemyInfo.split(" ");
                            EnemyHead[NoOfEnemy] = enHead[0];
                            NoOfEnemy++;
                        }
                    }
                }
                //calculate enemy disatnce from apple
                int[] EnemyDistanceFromApple = {0, 0, 0};
                for (int i = 0; i < NoOfEnemy; i++) {
                    String[] EnemyHeadCoor = EnemyHead[i].split(",");
                    int EHeadX = Integer.parseInt(EnemyHeadCoor[0]);
                    int EHeadY = Integer.parseInt(EnemyHeadCoor[1]);
                    EnemyDistanceFromApple[i] = BFStoFindApple(EHeadX, EHeadY, appleX, appleY, BoardSize);
                }

                boolean zCloseArray[] = {true,true,true};
                boolean zClosest = false;
                boolean zClose = false;
                for (int i=0; i<3; i++) {
                    String [] ZombieHeadCoor=ZombieHeads[i].split(",");
                    int ZombieHeadX=Integer.parseInt(ZombieHeadCoor[0]);
                    int ZombieHeadY=Integer.parseInt(ZombieHeadCoor[1]);
                    int MyDistance = BFStoFindDistance(headX,headY,ZombieHeadX,ZombieHeadY,BoardSize);
                    if (MyDistance<5) {
                        zClose = true;
                    }
                    for (int j=0; j<NoOfEnemy; j++) {
                        String [] EnemyHeadCoor=EnemyHead[j].split(",");
                        int EnemyHeadX=Integer.parseInt(EnemyHeadCoor[0]);
                        int EnemyHeadY=Integer.parseInt(EnemyHeadCoor[1]);
                        int closeDistance = BFStoFindDistance(EnemyHeadX,EnemyHeadY,ZombieHeadX,ZombieHeadY,BoardSize);
                        if (closeDistance<MyDistance) {
                            zCloseArray[i]=false;
                        }
                    }
                }
                if(zCloseArray[0] || zCloseArray[1] || zCloseArray[2]) {
                    zClosest = true;
                }

                for (int i = 0; i < NoOfEnemy; i++) {
                    String[] EnemyHeadCoor = EnemyHead[i].split(",");
                    int EnemyHeadX = Integer.parseInt(EnemyHeadCoor[0]);
                    int EnemyHeadY = Integer.parseInt(EnemyHeadCoor[1]);
                    if (EnemyHeadX < 49) {
                        BoardSize[EnemyHeadX + 1][EnemyHeadY] = 4;
                    }
                    if (EnemyHeadX > 0){
                        BoardSize[EnemyHeadX - 1][EnemyHeadY] = 4;
                    }
                    if (EnemyHeadY < 49){
                        BoardSize[EnemyHeadX][EnemyHeadY + 1] = 4;
                    }
                    if (EnemyHeadY > 0){
                        BoardSize[EnemyHeadX][EnemyHeadY - 1] = 4;
                    }
                }
                for (int i = 0; i < 6; i++) {
                    String[] ZombieHeadCoor = ZombieHeads[i].split(",");
                    int ZombieHeadX = Integer.parseInt(ZombieHeadCoor[0]);
                    int ZombieHeadY = Integer.parseInt(ZombieHeadCoor[1]);
                    if (ZombieHeadX < 49){
                        BoardSize[ZombieHeadX + 1][ZombieHeadY] = 4;
                    }
                    if (ZombieHeadX > 0){
                        BoardSize[ZombieHeadX - 1][ZombieHeadY] = 4;
                    }
                    if (ZombieHeadY < 49){
                        BoardSize[ZombieHeadX][ZombieHeadY + 1] = 4;
                    }
                    if (ZombieHeadY > 0){
                        BoardSize[ZombieHeadX][ZombieHeadY - 1] = 4;
                    }
                }
                int MyDistanceToApple = BFStoFindApple(headX, headY, appleX, appleY, BoardSize);
                int Distance = 0;
                for (int i = 0; i < NoOfEnemy; i++) {
                    if (MyDistanceToApple > EnemyDistanceFromApple[i]) {
                        Distance++;
                    }
                }


                //close to apple
                if (((Distance < 1) || (zClosest && length < 30)) && MyDistanceToApple < 1000) {
                    //run BFS
                    move = BFS(headX, headY, appleX, appleY, BoardSize);
                    //if path is not found
                    if (move == 4) {
                        move = GoForApple(appleX, appleY, Head, BoardSize, direction);
                    }
                    //far from apple
                } else {
                    if (appleX < 25 && appleY < 25) {
                        boolean b1 = BoardSize[36][36] == -1;
                        boolean b2 = false;
                        if (b1){
                            b2 = BFStoFindDistance(headX, headY, 36, 36, BoardSize) < 1000;
                        }
                        if (b1 && b2) {
                            BoardSize[appleX][appleY] = -1;
                            BoardSize[36][36] = -2;
                            appleX = 36;
                            appleY = 36;
                        }
                    } else if (appleX > 24 && appleY < 25) {
                        boolean b1 = BoardSize[12][36] == -1;
                        boolean b2 = false;
                        if (b1){
                            b2 = BFStoFindDistance(headX, headY, 12, 36, BoardSize) < 1000;
                        }
                        if (b1 && b2) {
                            BoardSize[appleX][appleY] = -1;
                            BoardSize[12][36] = -2;
                            appleX = 12;
                            appleY = 36;
                        }
                    } else if (appleX < 25 && appleY > 24) {
                        boolean b1 = BoardSize[36][12] == -1;
                        boolean b2 = false;
                        if (b1){
                            b2 = BFStoFindDistance(headX, headY, 36, 12, BoardSize) < 1000;
                        }
                        if (b1 && b2) {
                            BoardSize[appleX][appleY] = -1;
                            BoardSize[36][12] = -2;
                            appleX = 36;
                            appleY = 12;
                        }
                    } else if (appleX > 24 && appleY > 24) {
                        boolean b1 = BoardSize[12][12] == -1;
                        boolean b2 = false;
                        if (b1){
                            b2 = BFStoFindDistance(headX, headY, 12, 12, BoardSize) < 1000;
                        }
                        if (b1 && b2) {
                            BoardSize[appleX][appleY] = -1;
                            BoardSize[12][12] = -2;
                            appleX = 12;
                            appleY = 12;
                        }
                    }
                    move = BFS(headX, headY, appleX, appleY, BoardSize);
                    //if path is not found
                    if (move == 4) {
                        move = GoForApple(appleX, appleY, Head, BoardSize, direction);
                    }
                }
                System.out.println(move);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public  void drawSnake(int[][]grid, int snakeNum, String data){

        String[] vals = data.split(" ");
        int size = vals.length;
        for(int i=0; i<size-1; ++i){
            String a = vals[i];
            String b = vals[i+1];
            String[] out = a.split(",");
            String[] diff = b.split(",");
            int num=Integer.parseInt(diff[0]);
            int value=Integer.parseInt(out[0]);
            int count1=Integer.parseInt(diff[1]);
            int count2=Integer.parseInt(out[1]);

            if(count1==count2){
                int minx=Math.min(num,value);
                int maxx=Math.max(num,value);
                for(int h=minx;h<=maxx;++h){
                    grid[h][count1]=snakeNum;
                }
            }
            else if(num==value){
                int miny=Math.min(count1,count2);
                int maxy=Math.max(count1,count2);
                for(int j=miny;j<=maxy;++j){
                    grid[num][j]=snakeNum;
                }
            }
        }

    }

    public Boolean isSafe(int[][] Grid, String head, int nextMove) {
        String [] headCoords=head.split(",");
        int headX=Integer.parseInt(headCoords[0]);
        int headY=Integer.parseInt(headCoords[1]);
        int newX = headX;
        int newY = headY;
        int move = nextMove;
        int width = Grid.length;
        int height = Grid[0].length;

        if (move==0) {
            if (headY==0) {
                return false;
            }
            else newY--;
        }
        if (move==1) {
            if (headY==height-1) {
                return false;
            }
            else newY++;
        }
        if (move==2) {
            if (headX==0) {
                return false;
            }
            else newX--;
        }
        if (move==3) {
            if (headX==width-1) {
                return false;
            }
            else newX++;
        }

        return (Grid[newX][newY]==-1 || Grid[newX][newY]==-2);
    }

    public int BFS(int headX, int headY, int appleX, int appleY, int[][]Grid) {
        //initialize parent & visited arrays
        Cell[][] parent=new Cell[Grid.length][Grid[0].length];
        boolean [][]visited=new boolean[50][50];
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++)
            {
                if (Grid[i][j]==-1 || Grid[i][j]==-2 )
                    visited[i][j] = false;
                else
                    visited[i][j] = true;
            }
        }
        int move = 4;
        int nextX,nextY;
        //Run BFS
        LinkedList<Cell> q=new LinkedList<>();
        Cell headPos=new Cell(headX,headY);
        q.add(headPos);
        visited[headX][headY] = true;
        boolean found = false;
        while (!q.isEmpty()) {
            Cell p = q.getFirst();
            q.removeFirst();
            int x=p.getX();
            int y=p.getY();
            // Destination found;
            if (Grid[x][y] == -2) {
                found=true;
                break;
            }

            // moving up
            if (y - 1 >= 0 &&
                    visited[x][y-1] == false) {
                Cell next=new Cell(x,y-1);
                q.add(next);
                visited[x][y-1] = true;
                parent[x][y-1]=p;
            }

            // moving down
            if (y+ 1 < 50 &&
                    visited[x][y+1] == false) {
                Cell next=new Cell(x,y+1);
                q.add(next);
                visited[x][y+1] = true;
                parent[x][y+1]=p;
            }

            // moving left
            if (x - 1 >= 0 &&
                    visited[x-1][y] == false) {
                Cell next=new Cell(x-1,y);
                q.add(next);
                visited[x-1][y] = true;
                parent[x-1][y]=p;
            }

            // moving right
            if (x + 1 < 50 &&
                    visited[x+1][y] == false) {
                Cell next=new Cell(x+1,y);
                q.add(next);
                visited[x+1][y] = true;
                parent[x+1][y]=p;
            }

        }
        //if path is found track back
        if (found) {
            Cell prev=new Cell(appleX,appleY);
            Cell curr=parent[prev.getX()][prev.getY()];
            while(Grid[curr.getX()][curr.getY()]==-1) {
                prev=curr;
                curr=parent[curr.getX()][curr.getY()];
            }
            nextX=prev.getX();
            nextY=prev.getY();

            if(nextY<headY) {
                move=0;
            }
            else if(nextY>headY) {
                move=1;
            }
            else if(nextX<headX) {
                move=2;
            }
            else {
                move=3;
            }
        }

        return move;
    }

    public int BFStoFindApple(int headX, int headY, int appleX, int appleY, int[][]Grid) {
        //initialize parent & visited arrays
        Cell[][] parent=new Cell[Grid.length][Grid[0].length];
        boolean [][]visited=new boolean[50][50];
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++)
            {
                if (Grid[i][j]==-1 || Grid[i][j]==-2 )
                    visited[i][j] = false;
                else
                    visited[i][j] = true;
            }
        }
        int count = 1;
        //Run BFS
        LinkedList<Cell> q=new LinkedList<>();
        Cell headPos=new Cell(headX,headY);
        q.add(headPos);
        visited[headX][headY] = true;
        boolean found = false;
        while (!q.isEmpty()) {
            Cell p = q.getFirst();
            q.removeFirst();
            int x=p.getX();
            int y=p.getY();
            // Destination found;
            if (Grid[x][y] == -2) {
                found=true;
                break;
            }

            // moving up
            if (y - 1 >= 0 &&
                    visited[x][y-1] == false) {
                Cell next=new Cell(x,y-1);
                q.add(next);
                visited[x][y-1] = true;
                parent[x][y-1]=p;
            }

            // moving down
            if (y+ 1 < 50 &&
                    visited[x][y+1] == false) {
                Cell next=new Cell(x,y+1);
                q.add(next);
                visited[x][y+1] = true;
                parent[x][y+1]=p;
            }

            // moving left
            if (x - 1 >= 0 &&
                    visited[x-1][y] == false) {
                Cell next=new Cell(x-1,y);
                q.add(next);
                visited[x-1][y] = true;
                parent[x-1][y]=p;
            }

            // moving right
            if (x + 1 < 50 &&
                    visited[x+1][y] == false) {
                Cell next=new Cell(x+1,y);
                q.add(next);
                visited[x+1][y] = true;
                parent[x+1][y]=p;
            }

        }
        //if path is found track back
        if (found) {
            Cell prev=new Cell(appleX,appleY);
            Cell curr=parent[prev.getX()][prev.getY()];
            while(Grid[curr.getX()][curr.getY()]==-1) {
                prev=curr;
                curr=parent[curr.getX()][curr.getY()];
                count++;
            }
        }else {
            count=1000;
        }

        return count;
    }

    public int BFStoFindDistance(int startX, int startY, int endX, int endY, int[][]Grid) {
        //initialize parent & visited arrays
        Cell[][] parent=new Cell[Grid.length][Grid[0].length];
        boolean [][]visited=new boolean[50][50];
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++)
            {
                if (Grid[i][j]==-1 || Grid[i][j]==-2 )
                    visited[i][j] = false;
                else
                    visited[i][j] = true;
            }
        }
        int count = 1;
        int temp = Grid[endX][endY];
        Grid[endX][endY]=10;
        visited[endX][endY]=false;
        //Run BFS
        LinkedList<Cell> q=new LinkedList<>();
        Cell headPos=new Cell(startX,startY);
        q.add(headPos);
        visited[startX][startY] = true;
        boolean found = false;
        while (!q.isEmpty()) {
            Cell p = q.getFirst();
            q.removeFirst();
            int x=p.getX();
            int y=p.getY();
            // Destination found;
            if (Grid[x][y] == 10) {
                found=true;
                break;
            }

            // moving up
            if (y - 1 >= 0 &&
                    visited[x][y-1] == false) {
                Cell next=new Cell(x,y-1);
                q.add(next);
                visited[x][y-1] = true;
                parent[x][y-1]=p;
            }

            // moving down
            if (y+ 1 < 50 &&
                    visited[x][y+1] == false) {
                Cell next=new Cell(x,y+1);
                q.add(next);
                visited[x][y+1] = true;
                parent[x][y+1]=p;
            }

            // moving left
            if (x - 1 >= 0 &&
                    visited[x-1][y] == false) {
                Cell next=new Cell(x-1,y);
                q.add(next);
                visited[x-1][y] = true;
                parent[x-1][y]=p;
            }

            // moving right
            if (x + 1 < 50 &&
                    visited[x+1][y] == false) {
                Cell next=new Cell(x+1,y);
                q.add(next);
                visited[x+1][y] = true;
                parent[x+1][y]=p;
            }

        }
        //if path is found track back
        if (found) {
            Cell prev=new Cell(endX,endY);
            Cell curr=parent[prev.getX()][prev.getY()];
            while(Grid[curr.getX()][curr.getY()]==-1) {
                prev=curr;
                curr=parent[curr.getX()][curr.getY()];
                count++;
            }
        }else {
            count=1000;
        }

        Grid[endX][endY]=temp;

        return count;
    }

    public int GoForApple(int appleX, int appleY, String head, int[][] Grid, int direction) {
        String [] headCoords=head.split(",");
        int headX=Integer.parseInt(headCoords[0]);
        int headY=Integer.parseInt(headCoords[1]);
        int move = 0;
        if(appleX!=headX){
            //apple to the left
            if(appleX<headX) {
                //left safe
                if(isSafe(Grid,head,2)) {
                    //move left
                    move=2;
                }
                //left unsafe
                else{
                    //apple below
                    if(appleY>headY) {
                        //down safe
                        if(isSafe(Grid,head,1)) {
                            //move down
                            move=1;
                        }
                        //down unsafe
                        else {
                            //right safe
                            if(isSafe(Grid,head,3)) {
                                //move right
                                move=3;
                            }else {
                                //move up
                                move=0;
                            }
                        }
                    }
                    //apple above
                    else{
                        //up safe
                        if(isSafe(Grid,head,0)) {
                            //move up
                            move=0;
                        }
                        //up unsafe
                        else {
                            //right safe
                            if(isSafe(Grid,head,3)) {
                                //move right
                                move=3;
                            }else {
                                //move down
                                move=1;
                            }
                        }
                    }
                }
                //apple to the right
            }else if (appleX>headX) {
                //right safe
                if(isSafe(Grid,head,3)) {
                    //move right
                    move=3;
                }
                //right unsafe
                else{
                    //apple below
                    if(appleY>headY) {
                        //down safe
                        if(isSafe(Grid,head,1)) {
                            //move down
                            move=1;
                        }
                        //down unsafe
                        else {
                            //left safe
                            if(isSafe(Grid,head,2)) {
                                //move left
                                move=2;
                            }else {
                                //move up
                                move=0;
                            }
                        }
                    }
                    //apple above
                    else{
                        //up safe
                        if(isSafe(Grid,head,0)) {
                            //move up
                            move=0;
                        }
                        //up unsafe
                        else {
                            //left safe
                            if(isSafe(Grid,head,2)) {
                                //move left
                                move=2;
                            }else {
                                //move down
                                move=1;
                            }
                        }
                    }
                }
            }
        }
        //Same column, different row
        else if (appleY!=headY){
            //apple below
            if(appleY>headY) {
                //down safe
                if(isSafe(Grid,head,1)) {
                    //move down
                    move=1;
                }
                //down unsafe
                else{
                    //apple to the right
                    if(direction==0) {
                        //right safe
                        if(isSafe(Grid,head,3)) {
                            //move right
                            move=3;
                        }
                        //right unsafe
                        else {
                            //up safe
                            if(isSafe(Grid,head,2)) {
                                //move up
                                move=2;
                            }else {
                                //move left
                                move=0;
                            }
                        }
                    }
                    //apple to the left
                    else if(direction==2){
                        //left safe
                        if(isSafe(Grid,head,2)) {
                            //move left
                            move=2;
                        }
                        //left unsafe
                        else {
                            //up safe
                            if(isSafe(Grid,head,0)) {
                                //move up
                                move=0;
                            }else {
                                //move right
                                move=3;
                            }
                        }
                    }
                    else if(direction==3) {
                        if(isSafe(Grid,head,3)) {
                            //move left
                            move=3;
                        }
                        //left unsafe
                        else {
                            //up safe
                            if(isSafe(Grid,head,0)) {
                                //move up
                                move=0;
                            }else {
                                //move right
                                move=2;
                            }
                        }
                    }
                    else if(direction==1) {
                        if(isSafe(Grid,head,3)) {
                            //move left
                            move=3;
                        }
                        //left unsafe
                        else {
                            //up safe
                            if(isSafe(Grid,head,2)) {
                                //move up
                                move=2;
                            }else {
                                //move right
                                move=2;
                            }
                        }
                    }
                }
                //apple above
            }else if (appleY<headY) {
                //up safe
                if(isSafe(Grid,head,0)) {
                    //move up
                    move=0;
                }
                //up unsafe
                else{
                    //apple to the right
                    if(direction==1) {
                        //right safe
                        if(isSafe(Grid,head,3)) {
                            //move right
                            move=3;
                        }
                        //right unsafe
                        else {
                            //down safe
                            if(isSafe(Grid,head,1)) {
                                //move down
                                move=2;
                            }else {
                                //move left
                                move=1;
                            }
                        }
                    }
                    //apple to the left
                    else if(direction==2){
                        //left safe
                        if(isSafe(Grid,head,2)) {
                            //move left
                            move=2;
                        }
                        //left unsafe
                        else {
                            //down safe
                            if(isSafe(Grid,head,1)) {
                                //move down
                                move=1;
                            }else {
                                //move right
                                move=3;
                            }
                        }
                    }
                    else if(direction==3) {
                        if(isSafe(Grid,head,3)) {
                            //move left
                            move=3;
                        }
                        //left unsafe
                        else {
                            //down safe
                            if(isSafe(Grid,head,1)) {
                                //move down
                                move=1;
                            }else {
                                //move right
                                move=3;
                            }
                        }
                    }
                    else if(direction==0) {
                        if(isSafe(Grid,head,3)) {
                            //move left
                            move=3;
                        }
                        //left unsafe
                        else {
                            //down safe
                            if(isSafe(Grid,head,2)) {
                                //move down
                                move=2;
                            }else {
                                //move right
                                move=0;
                            }
                        }
                    }
                }
            }
        }
        return move;
    }
}