package com.example.iec61850goosegenerator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

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
    ScheduledExecutorService steadySendingThread = Executors.newSingleThreadScheduledExecutor();

//    ScheduledExecutorService transitionSendingThread = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture steadySendingTask;
    private Future<?> transitionSendingTask;
    //    private AtomicInteger stNumForSending = new AtomicInteger(0);
    private AtomicInteger sqNumForSending = new AtomicInteger(0);

    GoosePacket goosePacket = new GoosePacket();


    @FXML
    public void onStartButtonClick(ActionEvent actionEvent) {


        setGoosePacketByTextFields();
        AtomicInteger sqNumForSending = new AtomicInteger(0);

        goosePacket.setStNum(sqNumForSending.get());

        /*сделать проверку на null чтобы только один раз иницаилизировать*/

        sendingPacket.setNicName("VirtualBox Host-Only Ethernet Adapter" );

        sendingPacket.startInitialization();
        startSteadySending();

    }

    @FXML
    public void onStopButtonClick(ActionEvent actionEvent) {
        if (steadySendingTask!=null) {
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
         goosePacket.setStNum(0);
        goosePacket.setStNum(0);


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

    private void startTransitionSending(){
        steadySendingTask.cancel(true);
        steadySendingTask = null;

        long startTime = System.currentTimeMillis();
        long endTime = startTime + 2000;
        int executorCount = 2;
        if (transitionSendingTask == null) {
            while (System.currentTimeMillis() < endTime) {
                ScheduledExecutorService transitionSendingExecutors = Executors.newScheduledThreadPool(executorCount);

//                ExecutorService transitionSendingExecutors = Executors.newFixedThreadPool(executorCount);

                transitionSendingExecutors.scheduleWithFixedDelay(() -> sendingPacket.sendPackets(goosePacket),200,10,TimeUnit.MILLISECONDS);
//                transitionSendingTask = transitionSendingExecutors.submit(() -> sendingPacket.sendPackets(goosePacket));
             //   executorCount *= 2;
            }
            transitionSendingTask.cancel(true);
            transitionSendingTask = null;
            startSteadySending();



        }

    }


    private void startSteadySending(){
        if (steadySendingTask == null) {
            steadySendingTask = steadySendingThread.scheduleWithFixedDelay(() -> {
                sendingPacket.sendPackets(goosePacket);
                sqNumForSending.incrementAndGet();
                goosePacket.setSqNum(sqNumForSending.get());


            }, 1, 2, TimeUnit.SECONDS);
        }
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