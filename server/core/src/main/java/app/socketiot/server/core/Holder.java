package app.socketiot.server.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.socketiot.server.core.cli.ArgParser;
import app.socketiot.server.core.cli.properties.ServerProperties;
import app.socketiot.server.core.dao.BluePrintDao;
import app.socketiot.server.core.dao.DeviceDao;
import app.socketiot.server.core.dao.UserDao;
import app.socketiot.server.core.db.DB;
import app.socketiot.server.core.db.dao.BluePrintDBDao;
import app.socketiot.server.core.db.dao.DeviceDBDao;
import app.socketiot.server.core.db.dao.UserDBDao;
import app.socketiot.server.utils.JwtUtil;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Holder {
    private static Logger log = LogManager.getLogger();
    public final ServerProperties props;
    public final EventLoopGroup bossGroup;
    public final EventLoopGroup workerGroup;
    public final Class<? extends ServerChannel> channelClass;
    public final ArgParser args;
    public final SSLHandlerProvider sslprovider;
    public final DB db;
    public final UserDBDao userDBDao;
    public final UserDao userDao;
    public final DeviceDBDao deviceDBDao;
    public final DeviceDao deviceDao;
    public final BluePrintDao bluePrintDao;
    public final BluePrintDBDao bluePrintDBDao;
    public final BlockingIOHandler blockingIOHandler;
    public final JwtUtil jwtUtil;
    public final Mail mail;

    public Holder(ArgParser args) {
        this.args = args;
        this.props = new ServerProperties();
        this.sslprovider = new SSLHandlerProvider(this);
        int workerThreads = props.getIntProperty("server.worker.threads",
                Runtime.getRuntime().availableProcessors() * 2);
        if (Epoll.isAvailable()) {
            log.info("Using native epoll transport.");
            bossGroup = new EpollEventLoopGroup(1);
            workerGroup = new EpollEventLoopGroup(workerThreads);
            channelClass = EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(workerThreads);
            channelClass = NioServerSocketChannel.class;
        }
        this.db = new DB(this);
        this.userDBDao = new UserDBDao(this);
        this.deviceDBDao = new DeviceDBDao(this);
        this.bluePrintDBDao = new BluePrintDBDao(this);
        this.userDao = new UserDao(userDBDao.getAllUsers());
        this.deviceDao = new DeviceDao(deviceDBDao.getAllDevices());
        this.bluePrintDao = new BluePrintDao(bluePrintDBDao.getAllBluePrints());
        this.blockingIOHandler = new BlockingIOHandler();
        this.jwtUtil = new JwtUtil(props.getProperty("server.jwt.secret"));
        this.mail = new Mail(this);
    }

    public void close() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
