package org.duckdns.hjow.samples.space3D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.duckdns.hjow.samples.base.GUIProgram;
import org.duckdns.hjow.samples.base.GUISampleJavaCodes;
import org.duckdns.hjow.samples.base.SampleJavaCodes;
import org.duckdns.hjow.samples.util.UIUtil;

/** Swing 상에 3D 출력 예제 */
public class Space3D implements GUIProgram {
    private static final long serialVersionUID = -5808236352022744618L;
    protected JDialog dialog;
    protected Arena   arena;
    protected boolean threadSwitch = false;
    
    /** Space3D 기본 생성자이자 유일한 생성자입니다. */
    public Space3D(SampleJavaCodes superInstance) {
        super();
        init(superInstance);
    }
    
    @Override
    public void init(SampleJavaCodes superInstance) {
        if(dialog != null) dispose();
        
        dialog = new JDialog();
        dialog.setSize(600, 400);
        dialog.setTitle("Space 3D");
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        dialog.setIconImage(UIUtil.iconToImage(GUISampleJavaCodes.getDefaultIcon()));
        UIUtil.center(dialog);
        
        dialog.setLayout(new BorderLayout());
        
        JPanel pnRoot = new JPanel();
        pnRoot.setLayout(new BorderLayout());
        dialog.add(pnRoot, BorderLayout.CENTER);
        
        JToolBar toolbar = new JToolBar();
        pnRoot.add(toolbar, BorderLayout.NORTH);
        
        JLabel lb = new JLabel("카메라 수평이동 : W, A, S, D / 카메라 상하이동 : R, F / 객체 추가 : G / 초기화 : T / 종료 : Y");
        toolbar.add(lb);
        
        arena = new Arena();
        pnRoot.add(arena, BorderLayout.CENTER);
        
        dialog.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if(code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
                    arena.moveCamera(arena.getCameraX() + 1, arena.getCameraY(), arena.getCameraZ());
                } else if(code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
                    arena.moveCamera(arena.getCameraX() - 1, arena.getCameraY(), arena.getCameraZ());
                } else if(code == KeyEvent.VK_LEFT  || code == KeyEvent.VK_A) {
                    arena.moveCamera(arena.getCameraX(), arena.getCameraY() - 1, arena.getCameraZ());
                } else if(code == KeyEvent.VK_RIGHT  || code == KeyEvent.VK_D) {
                    arena.moveCamera(arena.getCameraX(), arena.getCameraY() + 1, arena.getCameraZ());
                } else if(code == KeyEvent.VK_Q) {
                    arena.rotateCamera(arena.getYaw() + Math.toRadians(1), arena.getPitch());
                } else if(code == KeyEvent.VK_E) {
                    arena.rotateCamera(arena.getYaw() - Math.toRadians(1), arena.getPitch());
                } else if(code == KeyEvent.VK_R) {
                    arena.moveCamera(arena.getCameraX(), arena.getCameraY(), arena.getCameraZ() + 1);
                } else if(code == KeyEvent.VK_F) {
                    arena.moveCamera(arena.getCameraX(), arena.getCameraY(), arena.getCameraZ() - 1);
                } else if(code == KeyEvent.VK_G) {
                    arena.addRandom();
                } else if(code == KeyEvent.VK_T) {
                    arena.reset();
                } else if(code == KeyEvent.VK_Y) {
                    arena.reset();
                }
            }
        });
    }
    
    // 쓰레드 내 작업
    protected void onThread() {
        arena.refresh();
    }

    @Override
    public void onBeforeOpened(SampleJavaCodes superInstance) { }

    @Override
    public void onAfterOpened(SampleJavaCodes superInstance) {
        threadSwitch = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(threadSwitch) {
                    onThread();
                    try { Thread.sleep(20L); } catch(InterruptedException e) { break; }
                }
            }
        }).start();
    }

    @Override
    public String getTitle() {
        return "Space 3D";
    }

    @Override
    public String getName() {
        return "space3D";
    }

    @Override
    public void log(String msg) {
        System.out.println(msg);
    }
    
    @Override
    public void alert(String msg) {
        JOptionPane.showMessageDialog(dialog, msg);
    }

    @Override
    public void open(SampleJavaCodes superInstance) {
        if(dialog == null) init(superInstance);
        dialog.setVisible(true);
        onAfterOpened(superInstance);
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void dispose() {
        if(dialog != null) {
            if(dialog.isVisible()) dialog.setVisible(false);
        }
        threadSwitch = false;
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public JDialog getDialog() {
        return dialog;
    }

}

/** 3D 출력 영역 */
class Arena extends JPanel {
    private static final long serialVersionUID = -4028755593279601526L;
    
    protected int w, h;
    protected double cameraX, cameraY, cameraZ;
    protected double yaw, pitch;
    protected Vector<Ovals> ovals = new Vector<Ovals>();
    
    public Arena() {
        super();
        reset();
    }
    public void refresh() {
        this.w = this.getWidth();
        this.h = this.getHeight();
        repaint();
    }
    public void moveCamera(double x, double y, double z) {
        this.cameraX = x;
        this.cameraY = y;
        this.cameraZ = z;
    }
    public void rotateCamera(double yaw, double pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        while(this.yaw   > Math.PI * 2) this.yaw   -= 2 * Math.PI; 
        while(this.pitch > Math.PI * 2) this.pitch -= 2 * Math.PI; 
        while(this.yaw   < 0) this.yaw   = 2 * Math.PI - this.yaw;
        while(this.pitch < 0) this.pitch = 2 * Math.PI - this.pitch;
    }
    public void addRandom() {
        ovals.add(new Ovals());
    }
    public void reset() {
        this.cameraX = 250;
        this.cameraY = 250;
        this.cameraZ = 250;
        this.yaw = 0.0;
        this.pitch = 0.0;
        ovals.clear();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Coordinate2D p;
        
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, w, h);
        for(Ovals ov : ovals) {
            p = ov.project(500.0, cameraX, cameraY, cameraZ, yaw, pitch, w / 2.0, h / 2.0);
            if(p == null) continue;
            
            g2d.setColor(Color.BLUE);
            g2d.fillOval((int) p.getX(), (int) p.getY(), (int) ov.getR(), (int) ov.getR());
        }
        
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("카메라 위치 : " + ((int) cameraX) + ", " + ((int) cameraY) + ", " + ((int) cameraZ), 10, 20);
        g2d.drawString("카메라 방향 : " + ((int) Math.toDegrees(yaw)) + ", " + ((int) Math.toDegrees(pitch)), 10, 30);
        g2d.drawString("객체 수     : " + ovals.size(), 10, 40);
    }
    public double getCameraX() {
        return cameraX;
    }
    public void setCameraX(double cameraX) {
        this.cameraX = cameraX;
    }
    public double getCameraY() {
        return cameraY;
    }
    public void setCameraY(double cameraY) {
        this.cameraY = cameraY;
    }
    public double getCameraZ() {
        return cameraZ;
    }
    public void setCameraZ(double cameraZ) {
        this.cameraZ = cameraZ;
    }
    public int getW() {
        return w;
    }
    public void setW(int w) {
        this.w = w;
    }
    public int getH() {
        return h;
    }
    public void setH(int h) {
        this.h = h;
    }
    public double getYaw() {
        return yaw;
    }
    public void setYaw(double yaw) {
        this.yaw = yaw;
    }
    public double getPitch() {
        return pitch;
    }
    public void setPitch(double pitch) {
        this.pitch = pitch;
    }
}

/** 2D 좌표계 */
class Coordinate2D {
    double x, y;
    public Coordinate2D() {}
    public Coordinate2D(double x, double y) { this.x = x; this.y = y; }
    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }
}

/** 객체 */
class Ovals {
    double x, y, z, r;
    public Ovals() {
        x = Math.abs(Math.random()) * 10000.0;
        y = Math.abs(Math.random()) * 10000.0;
        z = Math.abs(Math.random()) * 10000.0;
        r = Math.abs(Math.random()) * 100.0;
    }
    public Ovals(double x, double y, double z, double r) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }
    
    /** 2D 영역에 투영 (중앙 점만 투영) */
    public Coordinate2D project(double focalLength, double cameraX, double cameraY, double cameraZ, double yaw, double pitch, double screenCenterX, double screenCenterY) {
        double dx = getX() - cameraX;
        double dy = getY() - cameraY;
        double dz = getZ() - cameraZ;
        
        // yaw (Y축 회전)
        double cosY = Math.cos(yaw);
        double sinY = Math.sin(yaw);
        double tx = dx * cosY - dx * sinY;
        double tz = dx * sinY + dz * cosY;
        dx = tx;
        dz = tz;
        
        // pitch (X축 회전)
        double cosP = Math.cos(pitch);
        double sinP = Math.sin(pitch);
        double ty = dy * cosP - dz * sinP;
        tz = dy * sinP + dz * cosP;
        dy = ty;
        dz = tz;
        
        double cx = dx;
        double cy = dy;
        double cz = dz;
        
        if(cz <= 0) return null; // 카메라 뒤에 있는 경우 제외
        
        double px = focalLength * cx / cz;
        double py = focalLength * cy / cz;
        
        double u = px + screenCenterX;
        double v = py + screenCenterY;
        
        return new Coordinate2D(u, v);
    }
}
