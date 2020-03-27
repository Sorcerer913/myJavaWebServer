package com.company.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class Server extends Thread {
    Socket socket;
    LinkedList<String> lines;

    public Server(Socket socket) {

        this.socket = socket;

        setDaemon(true);

        start();

    }

    @Override
    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {


            // ждем первой строки запроса
            while (!input.ready());

            String uri = null;
            lines = new LinkedList<>();
            // считываем все что было отправлено клиентом
            System.out.println();
            while (input.ready()) {
                lines.addLast(input.readLine());
            }
            // печатаем все что было отправлено клиентом
            for (String line: lines){
                System.out.println(line);
            }

            //Server output..........................................
            work();
            //Server output..........................................

            // по окончанию выполнения блока try-with-resources потоки,
            // а вместе с ними и соединение будут закрыты
            System.out.println("Client disconnected!");

        } catch (IOException e) {
            System.out.println("Error: " + e.toString());
        }
    }

    private void work() throws IOException {
        PrintWriter output = new PrintWriter(socket.getOutputStream());
        // получаем, url
        String url = lines.getFirst().split(" ")[1];
        // Это нужно, чтобы попадать в директорию website
        url = "website/"+url;
        File file = new File(url);
        System.out.println("File: " +file.getPath());

        // проверяем на существование
        if (!file.exists()){
            output.println("HTTP/1.1 404 Not Found");
            output.println("Content-Type: text/html; charset=utf-8");
            output.println();
            printDefaultCode("404", output);
            output.println();
            output.flush();
            return;
        }

        // логируем расположение файла
        System.out.println("Absolute path: " + file.getAbsolutePath());

        // проверяем на дерикторию
        if(file.isDirectory()){
            url+= "index.html";
            file = new File(url);
        }
        //mime type check
        String path = url.substring(url.indexOf(".")+1);
        System.out.println(path);
        //TODO: nice answer and "accept-ranges: bytes" with working printWriter
        switch (path){
            case "html":
                output.println("HTTP/1.1 200 OK");
                output.println("Content-Type: text/html; charset=utf-8");
                System.out.println("HTTP/1.1 200 OK");
                System.out.println("Content-Type: text/html; charset=utf-8");
                break;
            case "png":
                output.println("HTTP/1.1 200 OK");
                output.println("Content-Type: image/png; charset=utf-8");
                break;
            case "jpg":
                output.println("HTTP/1.1 200 OK");
                output.println("Content-Type: image/jpg; charset=utf-8");
                break;
            case "jpeg":
                output.println("HTTP/1.1 200 OK");
                output.println("Content-Type: image/jpeg; charset=utf-8");
                break;
            case "gif":
                output.println("HTTP/1.1 200 OK");
                output.println("Content-Type: image/gif; charset=utf-8");
                break;
            case "js":
                output.println("HTTP/1.1 200 OK");
                output.println("Content-Type: text/js; charset=utf-8");
                break;
            case "css":
                output.println("HTTP/1.1 200 OK");
                output.println("Content-Type: text/css; charset=utf-8");
                break;
            default:
                output.println("HTTP/1.1 501 Not Implemented");
                output.println("Content-Type: text/html; charset=utf-8");
                output.println();
                printDefaultCode("501", output);
                output.println();
                output.flush();
                return;
        }

        output.println();
        printFile(file, output);
        output.println();
        output.flush();
        return;
    }

    private void printDefaultCode(String s, PrintWriter output) throws IOException {
        printFile(new File("website/httpcodes/"+s+".html"), output);
    }

    private void printFile(File file , PrintWriter output) throws
            IOException {
        System.out.println("Printing: " +file.getPath());
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            // парсим файл с выводом
            while ((line = reader.readLine()) != null) {
                output.println(line);
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.toString());
        }
    }

}
