#!/bin/sh
if [ $# -lt 3 ];then
  echo "retCode:-1"
  echo "retMes:Params Exception:[参数异常]"
  exit 1
fi

user_name=$1
password=$2
base_ftp_dir=$3
#操作类型：0删除   1添加   2修改
action_type=$4
vsftpd_dir="/etc/vsftpd"
#默认是添加用户
if [ ! -n "$action_type" ]; then
   action_type='1'
fi
#默认的错误返回信息
err_resp='retCode:-1
retMes:'

#入参用户在vuser.txt文件中的序号
usr_index=0
tmp_index=1
#读取所有奇数行，判断用户名是否已经存在
for txtusr in `awk 'NR%2' $vsftpd_dir/vuser.txt`  
do 
    if [ $txtusr = $user_name ]; then 
	usr_index=$tmp_index
        break
    fi
    let tmp_index=tmp_index+1
done
if [ $usr_index = "0" ]; then 
    #进入这个分支则vuser.txt中没有当前用户
    #没有这个用户，无论添加还是修改，都直接追加内容
    if [ $action_type != "0" ]; then
        echo $user_name >> $vsftpd_dir/vuser.txt
        echo $password >> $vsftpd_dir/vuser.txt
        echo "用户$user_name信息成功追加到vuser.txt文件中"
    else
        echo "$err_resp用户$user_name不存在，无法进行删除操作"
        exit 1
    fi
else
    if [ $action_type = "1" ]; then
	echo "$err_resp用户$user_name已经存在，无法创建同名用户，请换一个用户名"
        exit 1
    elif [ $action_type = "2"  ]; then
        let pwd_line=2*usr_index
        pwd_line_t="$pwd_line""c"
        sed -i  "$pwd_line_t $password" $vsftpd_dir/vuser.txt
        echo "成功将$user_name的密码修改"
    else 
        #获取原始密码
        let pwd_line=2*usr_index
        pwd_line_t="$pwd_line""p"
        old_pwd=`sed -n "$pwd_line_t" $vsftpd_dir/vuser.txt`
        if [ $old_pwd != $password ]; then
            echo "err_resp用户$user_name的密码不正确，无法进行删除操作"
            exit 1
        fi
        let usr_line=pwd_line-1
        del_command="$usr_line,$pwd_line""d"
        sed -i "$del_command" $vsftpd_dir/vuser.txt
        #删除不重要的文件
        #rm -rf $base_ftp_dir/$user_name
        rm -rf $vsftpd_dir/vsftpd_user_conf/$user_name
        echo "成功删除用户$user_name及相关文件"
    fi
fi 

#load to pam db
#删除老数据为了防止用户被删除后数据仍在
rm -rf $vsftpd_dir/vuser.db
db_load -T -t hash -f $vsftpd_dir/vuser.txt $vsftpd_dir/vuser.db

#如果是删除的话重新加载完就退出
if [ $action_type = "0"  ]
then
   echo "retCode:0"
   echo "retMes:用户$user_name及相关文件成功删除"
   exit 0
fi

#mkdir for virtual user
if [ ! -d "$base_ftp_dir/$user_name"  ];then
    mkdir "$base_ftp_dir/$user_name"
    #授权
    chmod 777 "$base_ftp_dir/$user_name"
fi
if [ ! -d "$vsftpd_dir/vsftpd_user_conf"  ];then
    mkdir "$vsftpd_dir/vsftpd_user_conf"
fi
#mkdir "$vsftpd_dir/vsftpd_user_conf"

if [ ! -f "$vsftpd_dir/vsftpd_user_conf/$user_name" ]; then
 touch $vsftpd_dir/vsftpd_user_conf/$user_name
 echo "anon_world_readable_only=NO" >> "$vsftpd_dir/vsftpd_user_conf/$user_name"
 echo "anon_upload_enable=YES" >> "$vsftpd_dir/vsftpd_user_conf/$user_name"
 echo "anon_mkdir_write_enable=YES" >> "$vsftpd_dir/vsftpd_user_conf/$user_name"
 echo "anon_other_write_enable=YES" >> "$vsftpd_dir/vsftpd_user_conf/$user_name"
 echo "local_root=$base_ftp_dir/$user_name" >> "$vsftpd_dir/vsftpd_user_conf/$user_name"
fi

echo "retCode:0"
echo "retMes:执行完毕!"
exit 0;