package com.company;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server extends Thread {

    Socket socket;

    public Server(Socket socket) {

        this.socket = socket;

        setDaemon(true);

        start();

    }

    @Override
    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter output = new PrintWriter(socket.getOutputStream())) {


            // ждем первой строки запроса
            while (!input.ready());

            String uri = null;

            // считываем и печатаем все что было отправлено клиентом
            System.out.println();
            while (input.ready()) {
                String line = input.readLine();
                if(line.split(" ")[0].equals("GET")) uri = line.split(" ")[1];
                System.out.println(line);
            }

            //TODO: обработка изображений и других запросов

            // Это нужно, чтобы попадать в директорию website
            uri = "website/"+uri;
            File file = new File(uri);

            // проверяем на существование
            if(file.exists()){
                System.out.println("File: Exists");

                // проверяем на дерикторию
                if (file.isDirectory()){
                    System.out.println("Is Directory");
                    file = new File(uri.substring(0, uri.length()-1)+ "index.html");
                }
                // логируем расположение файла
                System.out.println("Absolute path: " + file.getAbsolutePath());

                if (!file.exists()){
                    // отправляем сообщение об ошибке
                    output.println("HTTP/1.1 404 Not Found");
                    output.println("Content-Type: text/html; charset=utf-8");
                    output.println();
                    output.flush();

                }else{
                    // отправляем ответ
                    output.println("HTTP/1.1 200 OK");
                    output.println("Content-Type: text/html; charset=utf-8");
                    output.println();

                    // вывод файла
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    new FileInputStream(file), StandardCharsets.UTF_8))) {
                        String line;
                        // парсим файл с выводом
                        while ((line = reader.readLine()) != null) {
                            output.println(line);
                        }
//                        output.println("<h1>HelloWorld!!!<h1>");

                    } catch (IOException e) {
                        System.out.println("Error: " + e.toString());
                    }
                }

            // если такого файла не существует
            }else{
                // отправляем сообщение об ошибке
                output.println("HTTP/1.1 404 Not Found");
                output.println("Content-Type: text/html; charset=utf-8");
                output.println();
                output.flush();
            }

            // по окончанию выполнения блока try-with-resources потоки,
            // а вместе с ними и соединение будут закрыты
            System.out.println("Client disconnected!");


        } catch (IOException e) {
            System.out.println("Error: " + e.toString());
        }
    }

}
