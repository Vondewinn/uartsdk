package com.lc31.uartsdk;

import com.van.uart.LastError;
import com.van.uart.UartManager;

/*
* @author Vondewinn
* @date 2022-09-03
* */

public class SerialHelper {

    private UartManager uartManager;
    private ProcessData mProcessDataUart;
    private String port;
    private int baud;

    public SerialHelper(String port, int baud) {
        this.uartManager = new UartManager();
        this.port = port;
        this.baud = baud;
    }

    public boolean open() {
        try {
            uartManager.open(this.port, getBaudRate(this.baud));
            ReadThread readThread = new ReadThread();
            readThread.startMonitor();
            return true;
        } catch (LastError lastError) {
            lastError.printStackTrace();
            return false;
        }
    }

    public boolean close() {
        if (uartManager != null) {
            uartManager.close();
            return true;
        }
        return false;
    }

    public boolean isOpen() {
        return uartManager != null && uartManager.isOpen();
    }

    public void send(byte[] data) {
        if (uartManager != null) {
            try {
                uartManager.write(data, data.length);
            } catch (LastError lastError) {
                lastError.printStackTrace();
            }
        }
    }

    public void uartRevData(ProcessData processData) {
        this.mProcessDataUart = processData;
    }

    private class ReadThread implements Runnable {
        private Thread thread;

        public void startMonitor() {
            stopMonitor();
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }

        public void stopMonitor() {
            if (thread != null && thread.isAlive()) try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread = null;
        }

        @Override
        public void run() {
            byte[] rec = new byte[1024];
            while (uartManager.isOpen()) {
                int len;
                try {
                    len = uartManager.read(rec, rec.length, 0, 1);
                    if (len > 1) {
                        byte[] data = new byte[len];
                        System.arraycopy(rec, 0, data, 0, len);
                        mProcessDataUart.process(data, len);
                    }
                } catch (LastError lastError) {
                    lastError.printStackTrace();
                }
            }
        }
    }

    public UartManager.BaudRate getBaudRate(int baudrate) {
        UartManager.BaudRate value = null;
        switch (baudrate) {
            case 2400:
                value = UartManager.BaudRate.B2400;
                break;
            case 4800:
                value = UartManager.BaudRate.B4800;
                break;
            case 9600:
                value = UartManager.BaudRate.B9600;
                break;
            case 19200:
                value = UartManager.BaudRate.B19200;
                break;
            case 57600:
                value = UartManager.BaudRate.B57600;
                break;
            case 115200:
                value = UartManager.BaudRate.B115200;
                break;
            default:
                value = UartManager.BaudRate.B115200;
                break;
        }
        return value;
    }

}
