import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;


public class PelaporanPenyewa extends javax.swing.JFrame {

 // Variabel untuk menyimpan data login penyewa
    private int idPenyewa;
    private String namaPenyewa;

    // Constructor yang menerima parameter login
    public PelaporanPenyewa(int idPenyewa, String namaPenyewa) {
        this.idPenyewa = idPenyewa;
        this.namaPenyewa = namaPenyewa;
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Pelaporan Kerusakan - KosanKu");
        jLabelNama.setText("Halo, " + namaPenyewa + "!");

        // Set format untuk tanggal (dd-MM-yyyy)
        try {
            javax.swing.text.MaskFormatter formatter = new javax.swing.text.MaskFormatter("##-##-####");
            formatter.setPlaceholderCharacter('_');
            jFormattedTextFieldTanggalKejadianLapor.setFormatterFactory(
                new javax.swing.text.DefaultFormatterFactory(formatter));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private PelaporanPenyewa() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // Method untuk mendapatkan ID kontrak aktif penyewa
    private int getIdKontrakAktif() {
        String sql = "SELECT id_kontrak FROM kontrak_sewa WHERE id_penyewa = ? AND status_kontrak = 'aktif'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, this.idPenyewa);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_kontrak");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error: Gagal mengambil data kontrak\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }

    // Method untuk mengirim laporan
    private void kirimLaporan() {
        String tanggalKejadianStr = jFormattedTextFieldTanggalKejadianLapor.getText().trim();
        String deskripsiMasalah = jTextAreaDeskripsiLaporan.getText().trim();

        // Validasi input
        if (tanggalKejadianStr.contains("_")) {
            JOptionPane.showMessageDialog(this,
                "Silakan masukkan tanggal kejadian dengan format dd-mm-yyyy!",
                "Input Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (deskripsiMasalah.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Deskripsi masalah harus diisi!",
                "Input Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validasi format tanggal
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);
        java.util.Date tanggalKejadianDate;

        try {
            tanggalKejadianDate = sdf.parse(tanggalKejadianStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this,
                "Format tanggal tidak valid! Gunakan format dd-mm-yyyy",
                "Input Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Dapatkan ID kontrak aktif
        int idKontrak = getIdKontrakAktif();
        if (idKontrak == -1) {
            JOptionPane.showMessageDialog(this,
                "Tidak ditemukan kontrak aktif untuk akun Anda!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insert laporan ke database
        String sql = "INSERT INTO laporan_kerusakan (id_kontrak, tanggal_laporan, deskripsi_masalah) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idKontrak);
            stmt.setDate(2, new java.sql.Date(tanggalKejadianDate.getTime()));
            stmt.setString(3, deskripsiMasalah);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this,
                    "Laporan berhasil dikirim!\nLaporan akan segera ditangani oleh pemilik kos.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

                // Bersihkan form
                jFormattedTextFieldTanggalKejadianLapor.setValue(null);
                jTextAreaDeskripsiLaporan.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengirim laporan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: Gagal mengirim laporan\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButtonLogout = new javax.swing.JButton();
        jButtonPelaporan = new javax.swing.JButton();
        jButtonPembayaran = new javax.swing.JButton();
        jButtonKamarku = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabelNama = new javax.swing.JLabel();
        jButtonRiwayatLaporanPenyewa = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jFormattedTextFieldTanggalKejadianLapor = new javax.swing.JFormattedTextField();
        jButtonKirimLaporanPenyewa = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaDeskripsiLaporan = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(790, 480));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(150, 480));

        jPanel2.setMinimumSize(new java.awt.Dimension(150, 480));

        jButtonLogout.setText("Logout");
        jButtonLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonLogoutMouseClicked(evt);
            }
        });

        jButtonPelaporan.setText("Pelaporan");
        jButtonPelaporan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPelaporanActionPerformed(evt);
            }
        });

        jButtonPembayaran.setText("Pembayaran");
        jButtonPembayaran.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonPembayaranMouseClicked(evt);
            }
        });
        jButtonPembayaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPembayaranActionPerformed(evt);
            }
        });

        jButtonKamarku.setText("Kamarku");
        jButtonKamarku.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonKamarkuMouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("KOSANKU");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonPembayaran, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addComponent(jButtonKamarku, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonPelaporan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel2)
                .addGap(37, 37, 37)
                .addComponent(jButtonKamarku)
                .addGap(18, 18, 18)
                .addComponent(jButtonPembayaran)
                .addGap(18, 18, 18)
                .addComponent(jButtonPelaporan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 244, Short.MAX_VALUE)
                .addComponent(jButtonLogout)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 150, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabelNama.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelNama.setText("Halo, Nama!");
        getContentPane().add(jLabelNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, -1, -1));

        jButtonRiwayatLaporanPenyewa.setText("Cek Riwayat");
        jButtonRiwayatLaporanPenyewa.setPreferredSize(new java.awt.Dimension(72, 23));
        jButtonRiwayatLaporanPenyewa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonRiwayatLaporanPenyewaMouseClicked(evt);
            }
        });
        getContentPane().add(jButtonRiwayatLaporanPenyewa, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 430, 110, -1));

        jPanel3.setMinimumSize(new java.awt.Dimension(550, 330));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setText("Form Laporan Kerusakan");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setText("Tanggal Kejadian                                     :");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("Deskripsi Masalah                                   :");

        jFormattedTextFieldTanggalKejadianLapor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextFieldTanggalKejadianLaporActionPerformed(evt);
            }
        });

        jButtonKirimLaporanPenyewa.setText("Kirim");
        jButtonKirimLaporanPenyewa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonKirimLaporanPenyewaMouseClicked(evt);
            }
        });

        jTextAreaDeskripsiLaporan.setColumns(20);
        jTextAreaDeskripsiLaporan.setRows(5);
        jScrollPane1.setViewportView(jTextAreaDeskripsiLaporan);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(189, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addGap(190, 190, 190))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonKirimLaporanPenyewa, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jFormattedTextFieldTanggalKejadianLapor, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel15)
                .addGap(36, 36, 36)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jFormattedTextFieldTanggalKejadianLapor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel13))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonKirimLaporanPenyewa)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, 590, 330));

        jLabel1.setIcon(new javax.swing.ImageIcon("D:\\CIRA\\CODING\\LATIHAN\\Semester 4\\PBO\\PenyewaKosanKu\\src\\Assets\\white bg.png")); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 790, 480));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonPelaporanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPelaporanActionPerformed
        // TODO add your handling code here:
       
    }//GEN-LAST:event_jButtonPelaporanActionPerformed

    private void jButtonPembayaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPembayaranActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonPembayaranActionPerformed

    private void jFormattedTextFieldTanggalKejadianLaporActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextFieldTanggalKejadianLaporActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextFieldTanggalKejadianLaporActionPerformed

    private void jButtonKirimLaporanPenyewaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonKirimLaporanPenyewaMouseClicked
        // TODO add your handling code here:
        kirimLaporan();
    }//GEN-LAST:event_jButtonKirimLaporanPenyewaMouseClicked

    private void jButtonRiwayatLaporanPenyewaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonRiwayatLaporanPenyewaMouseClicked
        // TODO add your handling code here:
        new RiwayatLaporanPenyewa(this.idPenyewa, this.namaPenyewa).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButtonRiwayatLaporanPenyewaMouseClicked

    private void jButtonLogoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLogoutMouseClicked
        // TODO add your handling code here:
         int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin logout?",
            "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            UserSession.clearSession();
            new LoginPenyewa().setVisible(true);
            this.dispose();
        }
    
    }//GEN-LAST:event_jButtonLogoutMouseClicked

    private void jButtonKamarkuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonKamarkuMouseClicked
        // TODO add your handling code here:
        new DashboardPenyewa().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButtonKamarkuMouseClicked

    private void jButtonPembayaranMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonPembayaranMouseClicked
        // TODO add your handling code here:
        new PembayaranPenyewa(this.idPenyewa, this.namaPenyewa).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButtonPembayaranMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PelaporanPenyewa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PelaporanPenyewa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PelaporanPenyewa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PelaporanPenyewa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PelaporanPenyewa().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonKamarku;
    private javax.swing.JButton jButtonKirimLaporanPenyewa;
    private javax.swing.JButton jButtonLogout;
    private javax.swing.JButton jButtonPelaporan;
    private javax.swing.JButton jButtonPembayaran;
    private javax.swing.JButton jButtonRiwayatLaporanPenyewa;
    private javax.swing.JFormattedTextField jFormattedTextFieldTanggalKejadianLapor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelNama;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaDeskripsiLaporan;
    // End of variables declaration//GEN-END:variables
}
