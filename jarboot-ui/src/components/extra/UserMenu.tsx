import React, {memo, useState} from "react";
import {CommonConst} from "@/common/CommonConst";
import {Avatar, Menu, Popover} from "antd";
import {CaretDownOutlined, LogoutOutlined, FormOutlined, UserOutlined} from "@ant-design/icons";
import {useIntl} from "umi";
import ModifyUserModal from "@/components/extra/ModifyUserModal";
import CommonUtils from "@/common/CommonUtils";
import {DefaultUserIcon} from "@/components/icons";

const UserPopMenu = memo((props: any) => {
    const intl = useIntl();
    let [visible, setVisible] = useState(false);
    const SIGN_OUT_KEY = "sign-out";
    const MODIFY_PWD_KEY = "modify-password";
    const handleClick = (event: any) => {
        if (SIGN_OUT_KEY === event.key) {
            props.onHide && props.onHide();
            //用户注销
            CommonUtils.loginPage();
        }
        if (MODIFY_PWD_KEY === event.key) {
            props.onHide && props.onHide();
            //用户修改密码
            setVisible(true);
        }
    };
    const onClose = () => {
        setVisible(false);
    };
    const username = CommonConst.currentUser.username;
    return <>
        <Menu onClick={handleClick} selectedKeys={[]}
              defaultSelectedKeys={['user-title']} mode="inline"
              style={{width: 220}}>
            <Menu.Item key={"user-title"} icon={<UserOutlined/>}>{username}</Menu.Item>
            <Menu.Divider/>
            <Menu.Item key={MODIFY_PWD_KEY} icon={<FormOutlined />}>
                {intl.formatMessage({id: 'MODIFY_PWD'})}
            </Menu.Item>
            <Menu.Item key={SIGN_OUT_KEY} icon={<LogoutOutlined />}>
                {intl.formatMessage({id: 'SIGN_OUT'})}
            </Menu.Item>
        </Menu>
        {visible && <ModifyUserModal username={username}
                                     onClose={onClose} visible={visible}/>}
    </>;
});

interface UserMenuProp {
    className?: any;
}

const UserMenu = (props: UserMenuProp) => {
    let [userMenuVisible, setUserMenuVisible] = useState(false);
    const icon = <DefaultUserIcon style={{ fontSize: '28px' }}/>;
    return <Popover content={<UserPopMenu onHide={() => setUserMenuVisible(false)}/>}
                    visible={userMenuVisible}
                    mouseLeaveDelay={0.5}
                    onVisibleChange={(visible) => setUserMenuVisible(visible)}
                    placement="bottomRight">
        <Avatar className={props.className} alt={CommonConst.currentUser.username} icon={icon}/>
        <CaretDownOutlined style={{verticalAlign: 'text-top', position: "relative", top: '-2px'}}/>
    </Popover>;
};

export default UserMenu;
