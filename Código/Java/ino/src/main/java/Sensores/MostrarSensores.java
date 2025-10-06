package Sensores;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import org.bson.Document;
import java.util.List;
import java.util.ArrayList;

public class MostrarSensores {

    public static void main(String[] args) {
        List<Document> datos = ObtenerSensoresMongo.obtenerDatos();
        
        String[] columnas = {"Sensor", "Temperatura", "Humedad", "Fecha y Hora"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        
        agregarDatosATabla(datos, modelo);

        JTable tabla = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabla);

        tabla.getColumnModel().getColumn(1).setCellRenderer(new ColorTemperatureRenderer()); 
        tabla.getColumnModel().getColumn(2).setCellRenderer(new ColorHumidityRenderer()); 

        String[] opcionesFiltro = {"Todos", "Sensor 1", "Sensor 2"};
        JComboBox<String> comboBox = new JComboBox<>(opcionesFiltro);

        JButton botonFiltrar = new JButton("Filtrar");

        JFrame frame = new JFrame("Datos de Sensores");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 400); 
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS)); 

        frame.add(comboBox);
        frame.add(botonFiltrar);
        frame.add(scrollPane);

        botonFiltrar.addActionListener(e -> {
            String seleccion = (String) comboBox.getSelectedItem();
            List<Document> datosFiltrados = filtrarDatos(datos, seleccion);
            modelo.setRowCount(0); 
            agregarDatosATabla(datosFiltrados, modelo); 
        });

        frame.setVisible(true);
    }

    private static void agregarDatosATabla(List<Document> datos, DefaultTableModel modelo) {
        for (Document doc : datos) {
            Object[] fila = new Object[4];

            fila[0] = doc.getString("sensor");
            fila[1] = doc.get("temperatura");
            fila[2] = doc.get("humedad");
            fila[3] = doc.get("fecha_hora");

            modelo.addRow(fila);
        }
    }

    private static List<Document> filtrarDatos(List<Document> datos, String seleccion) {
        List<Document> datosFiltrados = new ArrayList<>();
        
        for (Document doc : datos) {
            String sensor = doc.getString("sensor");
            if (seleccion.equals("Todos") || seleccion.equals(sensor)) {
                datosFiltrados.add(doc);
            }
        }
        
        return datosFiltrados;
    }

    @SuppressWarnings("serial")
    private static class ColorTemperatureRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                Double temp = Double.valueOf(value.toString());
                double maxTemp = obtenerMaximaTemperatura(table);
                double minTemp = obtenerMinimaTemperatura(table);
                
                if (temp.equals(maxTemp)) {
                    comp.setBackground(Color.RED); 
                } else if (temp.equals(minTemp)) {
                    comp.setBackground(Color.CYAN); 
                } else {
                    comp.setBackground(Color.WHITE);
                }
            }
            return comp;
        }

        private double obtenerMaximaTemperatura(JTable table) {
            double maxTemp = Double.MIN_VALUE;
            for (int i = 0; i < table.getRowCount(); i++) {
                Double temp = Double.valueOf(table.getValueAt(i, 1).toString());
                if (temp > maxTemp) {
                    maxTemp = temp;
                }
            }
            return maxTemp;
        }

        private double obtenerMinimaTemperatura(JTable table) {
            double minTemp = Double.MAX_VALUE;
            for (int i = 0; i < table.getRowCount(); i++) {
                Double temp = Double.valueOf(table.getValueAt(i, 1).toString());
                if (temp < minTemp) {
                    minTemp = temp;
                }
            }
            return minTemp;
        }
    }

    @SuppressWarnings("serial")
    private static class ColorHumidityRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                Double humidity = Double.valueOf(value.toString());
                double maxHumidity = obtenerMaximaHumedad(table);
                double minHumidity = obtenerMinimaHumedad(table);
                
                if (humidity.equals(maxHumidity)) {
                    comp.setBackground(new Color(144, 238, 144)); 
                } else if (humidity.equals(minHumidity)) {
                    comp.setBackground(new Color(255, 255, 102)); 
                } else {
                    comp.setBackground(Color.WHITE); 
                }
            }
            return comp;
        }

        private double obtenerMaximaHumedad(JTable table) {
            double maxHumidity = Double.MIN_VALUE;
            for (int i = 0; i < table.getRowCount(); i++) {
                Double humidity = Double.valueOf(table.getValueAt(i, 2).toString());
                if (humidity > maxHumidity) {
                    maxHumidity = humidity;
                }
            }
            return maxHumidity;
        }

        private double obtenerMinimaHumedad(JTable table) {
            double minHumidity = Double.MAX_VALUE;
            for (int i = 0; i < table.getRowCount(); i++) {
                Double humidity = Double.valueOf(table.getValueAt(i, 2).toString());
                if (humidity < minHumidity) {
                    minHumidity = humidity;
                }
            }
            return minHumidity;
        }
    }
}