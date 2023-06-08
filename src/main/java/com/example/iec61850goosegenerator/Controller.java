package com.example.iec61850goosegenerator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import lombok.SneakyThrows;

import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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


    private SendingPackets sendingPacket = new SendingPackets();
    private ScheduledExecutorService steadySendingThread = Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService transitionSendingExecutors = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture steadySendingTask;
    private ScheduledFuture transitionSendingTask;
    private AtomicInteger stNumForSending = new AtomicInteger(0);
    private AtomicInteger sqNumForSending = new AtomicInteger(0);

    GoosePacket goosePacket = new GoosePacket();


    @FXML
    public void onStartButtonClick(ActionEvent actionEvent) {


        setGoosePacketByTextFields();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        checkTextFieldsCorrectness(alert);

        File dataFile = new File("com/example/iec61850goosegenerator/Data.txt" );
        if (!dataFile.exists()) {
            saveData();
        }


        sendingPacket.setNicName("VirtualBox Host-Only Ethernet Adapter" );

        sendingPacket.startInitialization();
        startSteadySending();

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
        goosePacket.setTimeAllowedtoLive(4805);
        goosePacket.setDatSet(datSet.getText());
        goosePacket.setGoID(goID.getText());

        goosePacket.setStNum(stNumForSending.get());
        goosePacket.setSqNum(sqNumForSending.get());


        goosePacket.setSimulation(Boolean.getBoolean(simulation.getText()));
        goosePacket.setConfRef(Integer.valueOf(confRef.getText()));
        goosePacket.setNdsCom(Boolean.getBoolean(ndsCom.getText()));

        /*тут соит сетить поля data в goosePacket*/

        boolean[] data = {Boolean.getBoolean(this.data.getText()), Boolean.getBoolean(data1.getText()), Boolean.getBoolean(data2.getText()), Boolean.getBoolean(data3.getText()), Boolean.getBoolean(data4.getText()), Boolean.getBoolean(data5.getText()), Boolean.getBoolean(data6.getText()), Boolean.getBoolean(data7.getText())};

        goosePacket.setNumDatSetEntries(8);
        goosePacket.setAllData(data);


        confRef.textProperty().addListener((observable, oldValue, newValue) -> {
                    goosePacket.setConfRef(Integer.valueOf(newValue));
                    startTransitionSending();

                }

        );


    }

    //

    private void checkTextFieldsCorrectness(Alert alert) {
        if (!macSrc.getText().matches("([a-zA-Z0-9]{2}:){5}[a-zA-Z0-9]{2}" )) {
            alert.setContentText("Invalid Src MAC" );
            alert.showAndWait();
        } else if (!macDst.getText().matches("([a-zA-Z0-9]{2}:){5}[a-zA-Z0-9]{2}" )) {
            alert.setContentText("Invalid Dst MAC" );
            alert.showAndWait();
        } else if (!gocbRef.getText().matches("[a-zA-Z0-9_]{1,}/LLN0\\$GO\\$[a-zA-Z0-9_]{1,}" )) {
            alert.setContentText("Invalid gocbRef" );
            alert.showAndWait();
        } else if (!datSet.getText().matches("[a-zA-Z0-9_]{1,}/LLN0\\$GOOSE[a-zA-Z0-9_]{1,}" )) {
            alert.setContentText("Invalid datSet" );
            alert.showAndWait();
        } else if (!goID.getText().matches("[A-Z0-9]{4}_[A-Z0-9]{3}_\\d{2}")) {
            alert.setContentText("Invalid GoID" );
            alert.showAndWait();
        } else if (!simulation.getText().equals("true" ) || !simulation.getText().equals("false" )) {
            alert.setContentText("Invalid simulation" );
            alert.showAndWait();
        } else if (!confRef.getText().matches("[0-9]{1,}" )) {
            alert.setContentText("Invalid confRef" );
            alert.showAndWait();
        } else if (!ndsCom.getText().equals("true" ) || !ndsCom.getText().equals("false" )) {
            alert.setContentText("Invalid ndsCom" );
            alert.showAndWait();
        } else if (!data.getText().equals("true" ) || !data.getText().equals("false" )) {
            alert.setContentText("Invalid Data" );
            alert.showAndWait();
        } else if (!data1.getText().equals("true" ) || !data1.getText().equals("false" )) {
            alert.setContentText("Invalid Data" );
            alert.showAndWait();
        } else if (!data2.getText().equals("true" ) || !data2.getText().equals("false" )) {
            alert.setContentText("Invalid Data" );
            alert.showAndWait();
        } else if (!data3.getText().equals("true" ) || !data3.getText().equals("false" )) {
            alert.setContentText("Invalid Data" );
            alert.showAndWait();
        } else if (!data4.getText().equals("true" ) || !data4.getText().equals("false" )) {
            alert.setContentText("Invalid Data" );
            alert.showAndWait();
        } else if (!data5.getText().equals("true" ) || !data5.getText().equals("false" )) {
            alert.setContentText("Invalid Data" );
            alert.showAndWait();
        } else if (!data6.getText().equals("true" ) || !data6.getText().equals("false" )) {
            alert.setContentText("Invalid Data" );
            alert.showAndWait();
        } else if (!data7.getText().equals("true" ) || !data7.getText().equals("false" )) {
            alert.setContentText("Invalid Data" );
            alert.showAndWait();
        }

    }


    @SneakyThrows
    private void startTransitionSending() {
        if (!steadySendingTask.isCancelled() && transitionSendingTask == null) {
            steadySendingTask.cancel(true);
            steadySendingTask = null;
            sqNumForSending.set(0);
            goosePacket.setSqNum(sqNumForSending.get());
            AtomicInteger cycleCount = new AtomicInteger(1);
            long startTime = System.currentTimeMillis();
            long endTime = startTime + 2000;

            transitionSendingTask = transitionSendingExecutors.scheduleWithFixedDelay(() -> {


                for (int i = 0; i < Math.pow(2, cycleCount.get()); i++) {
                    if (!(System.currentTimeMillis() < endTime)) {
                        break;
                    }
                    sendingPacket.sendPackets(goosePacket);
                    stNumForSending.incrementAndGet();
                    goosePacket.setStNum(stNumForSending.get());


                }
                cycleCount.incrementAndGet();

            }, 0, 10, TimeUnit.MILLISECONDS);
            Thread.sleep(2000);
            transitionSendingTask.cancel(true);
            transitionSendingTask = null;
            startSteadySending();

        }
    }


    private void startSteadySending() {
        if (steadySendingTask == null) {
            steadySendingTask = steadySendingThread.scheduleWithFixedDelay(() -> {
                sendingPacket.sendPackets(goosePacket);
                sqNumForSending.incrementAndGet();
                goosePacket.setSqNum(sqNumForSending.get());


            }, 1, 2, TimeUnit.SECONDS);
        }
    }


    public void saveData() {
        File file = new File("src/main/resources/com/example/iec61850goosegenerator/Data.txt" );
        try {
            file.createNewFile();
            PrintWriter pw = new PrintWriter(file);
            pw.println(macSrc.getText());
            pw.println(macDst.getText());
            pw.println(gocbRef.getText());
            pw.println(datSet.getText());
            pw.println(goID.getText());
            pw.println(simulation.getText());
            pw.println(confRef.getText());
            pw.println(ndsCom.getText());
            pw.println(data.getText());
            pw.println(data1.getText());
            pw.println(data2.getText());
            pw.println(data3.getText());
            pw.println(data4.getText());
            pw.println(data5.getText());
            pw.println(data6.getText());
            pw.println(data7.getText());
            pw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void pasteData(TextField[] textFieldsData) {


        BufferedReader br = null;
        try {


            br = new BufferedReader(new FileReader("src/main/resources/com/example/iec61850goosegenerator/Data.txt" ));
            String line;


            int i = 0;
            while ((line = br.readLine()) != null) {
                textFieldsData[i].setText(line);
                i++;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void insertDataButtonClick(ActionEvent actionEvent) {
        TextField[] textFieldsArray = {macSrc, macDst, gocbRef, datSet, goID, simulation, confRef, ndsCom, data, data1, data2, data3, data4, data5, data6, data7};

        pasteData(textFieldsArray);
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