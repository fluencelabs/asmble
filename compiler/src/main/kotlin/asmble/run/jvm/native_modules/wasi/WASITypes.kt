package asmble.run.jvm.native_modules.wasi

typealias __wasi_fd_t = Int;
val __WASI_STDIN_FILENO: __wasi_fd_t = 0;
val __WASI_STDOUT_FILENO: __wasi_fd_t = 1;
val __WASI_STDERR_FILENO: __wasi_fd_t = 2;

typealias __wasi_lookupflags_t = Int;
val __WASI_LOOKUP_SYMLINK_FOLLOW: __wasi_lookupflags_t = 1;

typealias __wasi_oflags_t = Int;//Short;
val __WASI_O_CREAT: __wasi_oflags_t = 1;
val __WASI_O_DIRECTORY: __wasi_oflags_t = 2;
val __WASI_O_EXCL: __wasi_oflags_t = 4;
val __WASI_O_TRUNC: __wasi_oflags_t = 8;

val __WASI_ESUCCESS = 0
val __WASI_E2BIG = 1
val __WASI_EACCES = 2
val __WASI_EADDRINUSE = 3
val __WASI_EADDRNOTAVAIL = 4
val __WASI_EAFNOSUPPORT = 5
val __WASI_EAGAIN = 6
val __WASI_EALREADY = 7
val __WASI_EBADF = 8
val __WASI_EBADMSG = 9
val __WASI_EBUSY = 10
val __WASI_ECANCELED = 11
val __WASI_ECHILD = 12
val __WASI_ECONNABORTED = 13
val __WASI_ECONNREFUSED = 14
val __WASI_ECONNRESET = 15
val __WASI_EDEADLK = 16
val __WASI_EDESTADDRREQ = 17
val __WASI_EDOM = 18
val __WASI_EDQUOT = 19
val __WASI_EEXIST = 20
val __WASI_EFAULT = 21
val __WASI_EFBIG = 22
val __WASI_EHOSTUNREACH = 23
val __WASI_EIDRM = 24
val __WASI_EILSEQ = 25
val __WASI_EINPROGRESS = 26
val __WASI_EINTR = 27
val __WASI_EINVAL = 28
val __WASI_EIO = 29
val __WASI_EISCONN = 30
val __WASI_EISDIR = 31
val __WASI_ELOOP = 32
val __WASI_EMFILE = 33
val __WASI_EMLINK = 34
val __WASI_EMSGSIZE = 35
val __WASI_EMULTIHOP = 36
val __WASI_ENAMETOOLONG = 37
val __WASI_ENETDOWN = 38
val __WASI_ENETRESET = 39
val __WASI_ENETUNREACH = 40
val __WASI_ENFILE = 41
val __WASI_ENOBUFS = 42
val __WASI_ENODEV = 43
val __WASI_ENOENT = 44
val __WASI_ENOEXEC = 45
val __WASI_ENOLCK = 46
val __WASI_ENOLINK = 47
val __WASI_ENOMEM = 48
val __WASI_ENOMSG = 49
val __WASI_ENOPROTOOPT = 50
val __WASI_ENOSPC = 51
val __WASI_ENOSYS = 52
val __WASI_ENOTCONN = 53
val __WASI_ENOTDIR = 54
val __WASI_ENOTEMPTY = 55
val __WASI_ENOTRECOVERABLE = 56
val __WASI_ENOTSOCK = 57
val __WASI_ENOTSUP = 58
val __WASI_ENOTTY = 59
val __WASI_ENXIO = 60
val __WASI_EOVERFLOW = 61
val __WASI_EOWNERDEAD = 62
val __WASI_EPERM = 63
val __WASI_EPIPE = 64
val __WASI_EPROTO = 65
val __WASI_EPROTONOSUPPORT = 66
val __WASI_EPROTOTYPE = 67
val __WASI_ERANGE = 68
val __WASI_EROFS = 69
val __WASI_ESPIPE = 70
val __WASI_ESRCH = 71
val __WASI_ESTALE = 72
val __WASI_ETIMEDOUT = 73
val __WASI_ETXTBSY = 74
val __WASI_EXDEV = 75
val __WASI_ENOTCAPABLE = 76

typealias __wasi_filetype_t = Int;//Byte;
val __WASI_FILETYPE_UNKNOWN: __wasi_filetype_t = 0;
val __WASI_FILETYPE_BLOCK_DEVICE: __wasi_filetype_t = 1;
val __WASI_FILETYPE_CHARACTER_DEVICE: __wasi_filetype_t = 2;
val __WASI_FILETYPE_DIRECTORY: __wasi_filetype_t = 3;
val __WASI_FILETYPE_REGULAR_FILE: __wasi_filetype_t = 4;
val __WASI_FILETYPE_SOCKET_DGRAM: __wasi_filetype_t = 5;
val __WASI_FILETYPE_SOCKET_STREAM: __wasi_filetype_t = 6;
val __WASI_FILETYPE_SYMBOLIC_LINK: __wasi_filetype_t = 7;

typealias __wasi_fstflags_t = Int;//Short;
val __WASI_FILESTAT_SET_ATIM: __wasi_fstflags_t = 1;
val __WASI_FILESTAT_SET_ATIM_NOW: __wasi_fstflags_t = 2;
val __WASI_FILESTAT_SET_MTIM: __wasi_fstflags_t = 4;
val __WASI_FILESTAT_SET_MTIM_NOW: __wasi_fstflags_t = 8;

typealias __wasi_fdflags_t = Int;//Short;
val __WASI_FDFLAG_APPEND: __wasi_fdflags_t = 1;
val __WASI_FDFLAG_DSYNC: __wasi_fdflags_t = 2;
val __WASI_FDFLAG_NONBLOCK: __wasi_fdflags_t = 4;
val __WASI_FDFLAG_RSYNC: __wasi_fdflags_t = 8;
val __WASI_FDFLAG_SYNC: __wasi_fdflags_t = 16;

typealias __wasi_rights_t = Long;
val __WASI_RIGHT_FD_DATASYNC: __wasi_rights_t = 1;
val __WASI_RIGHT_FD_READ: __wasi_rights_t = 2;
val __WASI_RIGHT_FD_SEEK: __wasi_rights_t = 4;
val __WASI_RIGHT_FD_FDSTAT_SET_FLAGS: __wasi_rights_t = 8;
val __WASI_RIGHT_FD_SYNC: __wasi_rights_t = 16;
val __WASI_RIGHT_FD_TELL: __wasi_rights_t = 32;
val __WASI_RIGHT_FD_WRITE: __wasi_rights_t = 64;
val __WASI_RIGHT_FD_ADVISE: __wasi_rights_t = 128;
val __WASI_RIGHT_FD_ALLOCATE: __wasi_rights_t = 256;
val __WASI_RIGHT_PATH_CREATE_DIRECTORY: __wasi_rights_t = 512;
val __WASI_RIGHT_PATH_CREATE_FILE: __wasi_rights_t = 1024;
val __WASI_RIGHT_PATH_LINK_SOURCE: __wasi_rights_t = 2048;
val __WASI_RIGHT_PATH_LINK_TARGET: __wasi_rights_t = 4096;
val __WASI_RIGHT_PATH_OPEN: __wasi_rights_t = 8192;
val __WASI_RIGHT_FD_READDIR: __wasi_rights_t = 16384;
val __WASI_RIGHT_PATH_READLINK: __wasi_rights_t = 32768;
val __WASI_RIGHT_PATH_RENAME_SOURCE: __wasi_rights_t = 65536;
val __WASI_RIGHT_PATH_RENAME_TARGET: __wasi_rights_t = 131072;
val __WASI_RIGHT_PATH_FILESTAT_GET: __wasi_rights_t = 262144;
val __WASI_RIGHT_PATH_FILESTAT_SET_SIZE: __wasi_rights_t = 524288;
val __WASI_RIGHT_PATH_FILESTAT_SET_TIMES: __wasi_rights_t = 1048576;
val __WASI_RIGHT_FD_FILESTAT_GET: __wasi_rights_t = 2097152;
val __WASI_RIGHT_FD_FILESTAT_SET_SIZE: __wasi_rights_t = 4194304;
val __WASI_RIGHT_FD_FILESTAT_SET_TIMES: __wasi_rights_t = 8388608;
val __WASI_RIGHT_PATH_SYMLINK: __wasi_rights_t = 16777216;
val __WASI_RIGHT_PATH_UNLINK_FILE: __wasi_rights_t = 33554432;
val __WASI_RIGHT_PATH_REMOVE_DIRECTORY: __wasi_rights_t = 67108864;
val __WASI_RIGHT_POLL_FD_READWRITE: __wasi_rights_t = 134217728;
val __WASI_RIGHT_SOCK_SHUTDOWN: __wasi_rights_t = 268435456;

data class __wasi_fdstat_t (
        var fs_filetype: __wasi_filetype_t,
        var fs_flags: __wasi_fdflags_t,
        var fs_rights_base: __wasi_rights_t,
        var fs_rights_inheriting: __wasi_rights_t
)
