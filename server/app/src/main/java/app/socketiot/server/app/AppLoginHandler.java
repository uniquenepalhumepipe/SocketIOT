package app.socketiot.server.app;

import app.socketiot.server.core.Holder;
import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.MsgType;
import app.socketiot.server.core.model.auth.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class AppLoginHandler extends ChannelInboundHandlerAdapter {
    private final Holder holder;

    public AppLoginHandler(Holder holder) {
        this.holder = holder;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HardwareMessage) {
            HardwareMessage message = (HardwareMessage) msg;
            if (message.body.length > 0) {
                String token = message.body[0];
                if (holder.jwtUtil.verifyToken(token)) {
                    String email = holder.jwtUtil.getEmail(token);
                    User user = holder.userDao.getUser(email);
                    if (user != null) {
                        user.json.addAppChannel(ctx.channel());
                        ctx.pipeline().replace(this, "AppHandler", new AppHandler(holder, user));
                        ctx.writeAndFlush(new HardwareMessage(MsgType.AUTH, "1"));
                        return;
                    }
                }
            }
            ctx.writeAndFlush(new HardwareMessage(MsgType.AUTH, "0"));
        }
    }
}
