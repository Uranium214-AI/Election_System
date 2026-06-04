import subprocess
import socket

def get_local_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        # Doesn't even have to be reachable
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
    except Exception:
        ip = "127.0.0.1"
    finally:
        s.close()
    return ip

ip = get_local_ip()
print(f"\n{'='*50}")
print(f"  VVS Election System — RUNNING")
print(f"  Open on this machine : http://localhost:8000")
print(f"  Other computers join : http://{ip}:8000")
print(f"{'='*50}\n")
subprocess.run(["python", "manage.py", "runserver", "0.0.0.0:8000"])
