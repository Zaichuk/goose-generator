package com.example.iec61850goosegenerator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import lombok.SneakyThrows;

import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Controller {
    @FXML
    private TextField macSrc;
    @FXML
    private TextField macDst;
    @FXML
    private TextField gocbRef;
    @FXML
    private TextField datSet;
    @FXML
    private TextField goID;
    @FXML
    private TextField simulation;
    @FXML
    private TextField confRef;
    @FXML
    private TextField ndsCom;
    @FXML
    private TextField data;
    @FXML
    private TextField data1;
    @FXML
    private TextField data2;
    @FXML
    private TextField data3;
    @FXML
    private TextField data4;
    @FXML
    private TextField data5;
    @FXML
    private TextField data6;
    @FXML
    private TextField data7;


    private final SendingPackets sendingPacket = new SendingPackets();
    private final ScheduledExecutorService steadySendingThread = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService transitionSendingExecutors = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture steadySendingTask;
    private ScheduledFuture transitionSendingTask;
    private AtomicInteger stNumForSending = new AtomicInteger(1);
    private AtomicInteger sqNumForSending = new AtomicInteger(1);
    private boolean isDataCorrect;


    GoosePacket goosePacket = new GoosePacket();


    @FXML
    public void onStartButtonClick(ActionEvent actionEvent) {


        Alert alert = new Alert(Alert.AlertType.ERROR);
        checkTextFieldsCorrectness(alert);

//        goosePacket.setTimeAllowedtoLive(4805);
//        goosePacket.setNumDatSetEntries(8);


        if (isDataCorrect) {
            setGoosePacketByTextFields();
            TextField[] textFieldsArray = {macSrc, macDst, gocbRef, datSet, goID, simulation, confRef, ndsCom, data, data1, data2, data3, data4, data5, data6, data7};


            File dataFile = new File("com/example/iec61850goosegenerator/Data.txt" );
            if (!dataFile.exists()) {
                saveData(textFieldsArray);
            }


            sendingPacket.setNicName("VirtualBox Host-Only Ethernet Adapter" );

            sendingPacket.startInitialization();
            startSteadySending();
        }
    }

    @FXML
    public void onStopButtonClick(ActionEvent actionEvent) {
        if (steadySendingTask != null) {
            steadySendingTask.cancel(true);
            steadySendingTask = null;
        }
        if (transitionSendingTask != null) {
            transitionSendingTask.cancel(true);
            transitionSendingTask = null;
        }
    }


    private void setGoosePacketByTextFields() {

//        TextField[] textFieldsArray = new TextField[]{macDst, macSrc, gocbRef, datSet, goID, data, data1, data2, data3, data4, data5, data6, data7};
//        Class<? extends GoosePacket> aClass = goosePacket.getClass();
//        Field[] declaredFields = aClass.getDeclaredFields();
//
//
//        for (int i = 0; i < textFieldsArray.length; i++) {
//            int finalI = i;
//            textFieldsArray[i].textProperty().addListener((observable, oldValue, newValue) -> {
//
//                Field field = declaredFields[finalI];
//                if (textFieldsArray[finalI].getText().equals(field.getName())) {
//                    field.setAccessible(true);
//
//                    if (field.getType().getSimpleName().equals(int.class.getSimpleName())) {
//                        try {
//                            /* тип new value и тип поля goosePacket совпадают?*/
//
//                            field.set(goosePacket, Integer.valueOf(newValue));
//                        } catch (IllegalAccessException e) {
//                            throw new RuntimeException(e);
//                        }
//                    } else if (field.getType().getSimpleName().equals(String.class.getSimpleName())) {
//
//                        try {
//                            /* тип new value и тип поля goosePacket совпадают?*/
//
//                            field.set(goosePacket, newValue);
//                        } catch (IllegalAccessException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                    }else {
//                        try {
//                            /* тип new value и тип поля goosePacket совпадают?*/
//
//                            field.set(goosePacket, Boolean.valueOf(newValue));
//                        } catch (IllegalAccessException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//
//
//                }
//
//
//                    }
//
//            );
//        }
        /*я хочу чтобы по изменеию новое значени добавлялосмь в пакет и запускался поток */
        goosePacket.setMacDst(macDst.getText());
        goosePacket.setMacSrc(macSrc.getText());

        goosePacket.setGocbRef(gocbRef.getText());
        goosePacket.setTimeAllowedtoLive(3000);
        goosePacket.setDatSet(datSet.getText());
        goosePacket.setGoID(goID.getText());

        goosePacket.setStNum(stNumForSending.get());
        goosePacket.setSqNum(sqNumForSending.get());


        goosePacket.setSimulation(Boolean.valueOf(simulation.getText()));
        goosePacket.setConfRef(Integer.valueOf(confRef.getText()));
        goosePacket.setNdsCom(Boolean.valueOf(ndsCom.getText()));

        /*тут соит сетить поля data в goosePacket*/

//        boolean[] data = {Boolean.getBoolean(this.data.getText()), Boolean.getBoolean(data1.getText()), Boolean.getBoolean(data2.getText()), Boolean.getBoolean(data3.getText()), Boolean.getBoolean(data4.getText()), Boolean.getBoolean(data5.getText()), Boolean.getBoolean(data6.getText()), Boolean.getBoolean(data7.getText())};


        goosePacket.setData(Boolean.valueOf(data.getText()));
        goosePacket.setData1(Boolean.valueOf(data1.getText()));
        goosePacket.setData2(Boolean.valueOf(data2.getText()));
        goosePacket.setData3(Boolean.valueOf(data3.getText()));
        goosePacket.setData4(Boolean.valueOf(data4.getText()));
        goosePacket.setData5(Boolean.valueOf(data5.getText()));
        goosePacket.setData6(Boolean.valueOf(data6.getText()));
        goosePacket.setData7(Boolean.valueOf(data7.getText()));


        goosePacket.setNumDatSetEntries(8);
        //   goosePacket.setAllData(data);


    }


    private void checkTextFieldsCorrectness(Alert alert) {

        if (!checkMacAdressCorrectness(macSrc.getText())) {
            alert.setContentText("Invalid Src MAC" );
            alert.showAndWait();
            isDataCorrect = false;
        } else if (!checkMacAdressCorrectness(macDst.getText())) {
            alert.setContentText("Invalid Dst MAC" );
            alert.showAndWait();
            isDataCorrect = false;

        } else if (!checkGocbRefCorrectness(gocbRef.getText())) {
            alert.setContentText("Invalid gocbRef" );
            alert.showAndWait();
            isDataCorrect = false;

        } else if (!checkDatSetCorrectness(datSet.getText())) {
            alert.setContentText("Invalid datSet" );
            alert.showAndWait();
            isDataCorrect = false;

        } else if (!checkGoIdCorrectness(goID.getText())) {
            alert.setContentText("Invalid GoID" );
            alert.showAndWait();
            isDataCorrect = false;

        } else if (!checkBooleanTextCorrectness(simulation.getText())) {
            alert.setContentText("Invalid simulation" );
            alert.showAndWait();
            isDataCorrect = false;

        } else if (!checkIntegerTextCorrectness(confRef.getText())) {
            alert.setContentText("Invalid confRef" );
            alert.showAndWait();
            isDataCorrect = false;

        } else if (!checkBooleanTextCorrectness(ndsCom.getText())) {
            alert.setContentText("Invalid ndsCom" );
            alert.showAndWait();
            isDataCorrect = false;

        } else if (!checkBooleanTextCorrectness(data.getText())) {
            alert.setContentText("Invalid Data" );
            alert.showAndWait();
            isDataCorrect = false;

        } else if (!checkBooleanTextCorrectness(data1.getText())) {
            alert.setContentText("Invalid Data" );
            alert.showAndWait();
            isDataCorrect = false;

        } else if (!checkBooleanTextCorrectness(data2.getText())) {
            alert.setContentText("Invalid Data" );
            alert.showAndWait();
            isDataCorrect = false;

        } else if (!checkBooleanTextCorrectness(data3.getText())) {
            alert.setContentText("Invalid Data" );
            alert.showAndWait();
            isDataCorrect = false;

        } else if (!checkBooleanTextCorrectness(data4.getText())) {
            alert.setContentText("Invalid Data" );
            alert.showAndWait();
            isDataCorrect = false;

        } else if (!checkBooleanTextCorrectness(data5.getText())) {
            alert.setContentText("Invalid Data" );
            alert.showAndWait();
            isDataCorrect = false;

        } else if (!checkBooleanTextCorrectness(data6.getText())) {
            alert.setContentText("Invalid Data" );
            alert.showAndWait();
            isDataCorrect = false;

        } else if (!checkBooleanTextCorrectness(data7.getText())) {
            alert.setContentText("Invalid Data" );
            alert.showAndWait();
            isDataCorrect = false;

        } else {

            isDataCorrect = true;
        }
//
    }

    //!steadySendingTask.isCancelled()
    @SneakyThrows
    private void startTransitionSending() {
        if (steadySendingTask != null && transitionSendingTask == null) {
            while (!steadySendingTask.isCancelled()) {
                if (steadySendingTask.getDelay(TimeUnit.MILLISECONDS) > 1998) {
                    steadySendingTask.cancel(true);

                }
            }
            steadySendingTask = null;
//            steadySendingTask.cancel(true);
//            steadySendingTask = null;
            goosePacket.setT(GoosePacket.getCurrentTimeForT());
            sqNumForSending.set(0);
            goosePacket.setSqNum(sqNumForSending.get());


            AtomicInteger cycleCount = new AtomicInteger(2);
//            AtomicInteger time = new AtomicInteger(2);
//
//            long startTime = System.currentTimeMillis();
//            long endTime = startTime + 2000;
//
//            Lock lock1 = new ReentrantLock();
//            transitionSendingTask = transitionSendingExecutors.schedule(() -> {
//
//               // lock1.lock();
///*МБ СДЕЛАТЬ i в lock*/
//                for (int i = 2; i <= 2049; i *= 2) {
//
//                    if (i == 2048) {
//                        i = i - 48;
//                    }
//
//
//                    try {
//
//                        stNumForSending.incrementAndGet();
//                        goosePacket.setStNum(stNumForSending.get());
//                        goosePacket.setTimeAllowedtoLive((int) (i * 1.5));
//                        sendingPacket.sendPackets(goosePacket);
//     //                   Thread.sleep(i);
//
//                       if (i!=2) {
//                           Thread.sleep(i);
//                       }
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
////                lock1.unlock();
//
//
//            }, 0, TimeUnit.MILLISECONDS);

            Thread thread = new Thread(() -> {

                while (cycleCount.get() < 2049) {


                    try {
                        if (cycleCount.get() == 2048) {
                            cycleCount.set(cycleCount.get() - 48);
                        }
                        if (cycleCount.get() != 2) {
                            Thread.sleep(cycleCount.get());
                        }
                        stNumForSending.incrementAndGet();
                        goosePacket.setStNum(stNumForSending.get());
                        goosePacket.setTimeAllowedtoLive((int) (cycleCount.get() * 1.5));
                        sendingPacket.sendPackets(goosePacket);
                        cycleCount.set(cycleCount.get() * 2);

//                       if (i!=2) {
//                           Thread.sleep(i);
//                       }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

            Thread.sleep(6064);//4046
            //transitionSendingTask.cancel(true);
            transitionSendingTask = null;
            startSteadySending();

//
//            transitionSendingTask = transitionSendingExecutors.scheduleWithFixedDelay(() -> {
//
//
////                for (int i = 0; i < Math.pow(2, cycleCount.get()); i++) {
////                    if (!(System.currentTimeMillis() < endTime)) {
////                        break;
////                    }
//                if (time.get()==2000) {
//                    transitionSendingTask.cancel(true);
//                    transitionSendingTask = null;
//                    startSteadySending();
//
//                }
//                    time.set((int) Math.pow(time.get(),cycleCount.get()));
//                    sendingPacket.sendPackets(goosePacket);
//                    stNumForSending.incrementAndGet();
//                    goosePacket.setStNum(stNumForSending.get());
//
//
//
//                cycleCount.incrementAndGet();
//
//            }, 0, time.get(), TimeUnit.MILLISECONDS);
//            Thread.sleep(2000);
//            transitionSendingTask.cancel(true);
//            transitionSendingTask = null;
//            startSteadySending();

        }
    }


    private void startSteadySending() {
        if (steadySendingTask == null) {
            goosePacket.setTimeAllowedtoLive(3000);
            steadySendingTask = steadySendingThread.scheduleWithFixedDelay(() -> {
                sqNumForSending.incrementAndGet();
                goosePacket.setSqNum(sqNumForSending.get());
                sendingPacket.sendPackets(goosePacket);


            }, 0, 2, TimeUnit.SECONDS);
        }
    }


    public void saveData(TextField[] textFields) {


        try {


            FileWriter writer = new FileWriter("src/main/resources/com/example/iec61850goosegenerator/Data.txt" );


            for (TextField tf : textFields) {
                writer.write(tf.getText());
                writer.write(System.lineSeparator());

            }


            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void pasteData(TextField[] textFieldsData) {


        try {
            FileReader fileReader = new FileReader("src/main/resources/com/example/iec61850goosegenerator/Data.txt" );
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {
                textFieldsData[i].setText(line);
                i++;

            }
            bufferedReader.close();
            fileReader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @FXML
    public void insertDataButtonClick(ActionEvent actionEvent) {
        TextField[] textFieldsArray = {macSrc, macDst, gocbRef, datSet, goID, simulation, confRef, ndsCom, data, data1, data2, data3, data4, data5, data6, data7};
        pasteData(textFieldsArray);
    }

    @FXML
    public void initialize() {


        data.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && checkBooleanTextCorrectness(data.getText())) {
                goosePacket.setData(Boolean.valueOf(data.getText()));
                startTransitionSending();

            }
        });


        data1.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && checkBooleanTextCorrectness(data1.getText())) {
                goosePacket.setData(Boolean.valueOf(data1.getText()));
                startTransitionSending();

            }
        });


        data2.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && checkBooleanTextCorrectness(data2.getText())) {
                goosePacket.setData(Boolean.valueOf(data2.getText()));
                startTransitionSending();

            }
        });

        data3.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && checkBooleanTextCorrectness(data3.getText())) {
                goosePacket.setData(Boolean.valueOf(data3.getText()));
                startTransitionSending();

            }
        });

        data4.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && checkBooleanTextCorrectness(data4.getText())) {
                goosePacket.setData(Boolean.valueOf(data4.getText()));
                startTransitionSending();

            }
        });

        data5.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && checkBooleanTextCorrectness(data5.getText())) {
                goosePacket.setData(Boolean.valueOf(data5.getText()));
                startTransitionSending();

            }
        });

        data6.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && checkBooleanTextCorrectness(data6.getText())) {
                goosePacket.setData(Boolean.valueOf(data6.getText()));
                startTransitionSending();

            }
        });

        data7.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && checkBooleanTextCorrectness(data7.getText())) {
                goosePacket.setData(Boolean.valueOf(data7.getText()));
                startTransitionSending();

            }
        });


    }

    private boolean checkMacAdressCorrectness(String s) {
        return s.matches("([a-zA-Z0-9]{2}:){5}[a-zA-Z0-9]{2}" );
    }

    private boolean checkGocbRefCorrectness(String s) {
        return s.matches("[a-zA-Z0-9_]{1,}/LLN0\\$GO\\$[a-zA-Z0-9_]{1,}" );
    }


    private boolean checkDatSetCorrectness(String s) {
        return s.matches("[a-zA-Z0-9_]{1,}/LLN0\\$GOOSE[a-zA-Z0-9_]{1,}" );
    }


    private boolean checkGoIdCorrectness(String s) {
        return s.matches("[A-Z0-9]{4}_[A-Z0-9]{3}_\\d{2}" );
    }


    private boolean checkIntegerTextCorrectness(String s) {
        return s.matches("[0-9]{1,}" );
    }

    private boolean checkBooleanTextCorrectness(String s) {
        return s.matches("^(true|false)$" );
    }
//
//    private void addListenerForTextFields() {
//        destField.textProperty().addListener((observable, oldValue, newValue) -> {
//            if (transitionSendingTask == null) {
//                steadySendingTask.cancel(true);
//
//                transitionSendingTask = transitionSendingThread.scheduleWithFixedDelay(() -> {
//                    sendingPacket.sendPackets(goosePacket);
//                    sqNumForSending.incrementAndGet();
//                    if (stNumForSending.get() !=0) {
//                        stNumForSending.set(0);
//                    }
//
//                    goosePacket.setStNum(sqNumForSending.get());
//
//
//                }, 0, 2, TimeUnit.MILLISECONDS);
//            }
//        });
//
//    }
}