This application is a mobile application to transcribe uploaded audio to sheet music.
It uses https://github.com/spotify/basic-pitch to identify the notes.

To run the application, update app.py in the repository to the following

```
from flask import Flask, request, jsonify, send_from_directory
import os
import pathlib
import sys
import subprocess
import time
import socket

try:
    import basic_pitch
    from basic_pitch.inference import predict
    from basic_pitch import ICASSP_2022_MODEL_PATH, ONNX_PRESENT
except ImportError:
    print("ERROR: basic-pitch not found. Run 'pip install -e .' in this folder.")
    sys.exit(1)

def get_local_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        s.connect(('10.255.255.255', 1))
        IP = s.getsockname()[0]
    except Exception:
        IP = '127.0.0.1'
    finally:
        s.close()
    return IP

app = Flask(__name__)
LOCAL_IP = get_local_ip()
PORT = 5000

MUSESCORE_PATH = r"C:\Program Files\MuseScore 4\bin\MuseScore4.exe"
BASE_DIR = pathlib.Path(__file__).parent.absolute()
MODEL_PATH = BASE_DIR / "basic_pitch" / "saved_models" / "icassp_2022" / "nmp.onnx"
OUTPUT_DIR = BASE_DIR / "outputs"
os.makedirs(OUTPUT_DIR, exist_ok=True)

@app.route('/api', methods=['POST'])
def transcribe():
    if 'file' not in request.files:
        return jsonify({"success": False, "msg": "No file part"})

    file = request.files['file']
    session_id = int(time.time())
    input_path = os.path.join(BASE_DIR, f"input_{session_id}.wav")
    file.save(input_path)

    try:
        print(f"Transcribing audio...")
        model_output, midi_data, note_events = predict(input_path, model_or_model_path=str(MODEL_PATH))

        midi_filename = f"score_{session_id}.mid"
        midi_path = OUTPUT_DIR / midi_filename
        midi_data.write(str(midi_path))

        score_image_filename = f"score_{session_id}.png"
        score_image_path = OUTPUT_DIR / score_image_filename
        has_score_image = False

        if os.path.exists(MUSESCORE_PATH):
            subprocess.run([MUSESCORE_PATH, "-o", str(score_image_path), str(midi_path)], check=True)
            actual_png = OUTPUT_DIR / f"score_{session_id}-1.png"
            if actual_png.exists():
                os.rename(actual_png, score_image_path)
            has_score_image = os.path.exists(score_image_path)

        formatted_notes = []
        for start, end, pitch, amp, _ in note_events:
            formatted_notes.append({
                "pitch": int(pitch), "start": float(start), "end": float(end), "velocity": int(amp * 127)
            })

        return jsonify({
            "success": True,
            "pdf_url": None,
            "others_url": f"http://{LOCAL_IP}:{PORT}/download/{midi_filename}",
            "score_image_url": f"http://{LOCAL_IP}:{PORT}/download/{score_image_filename}" if has_score_image else None,
            "notes": formatted_notes,
            "msg": "Success"
        })
    except Exception as e:
        print(f"Error: {str(e)}")
        return jsonify({"success": False, "msg": str(e)})

@app.route('/download/<filename>')
def download_file(filename):
    return send_from_directory(OUTPUT_DIR, filename)

if __name__ == '__main__':
    print(f"--- Basic Pitch Server ---")
    print(f"Access from phone at: http://{LOCAL_IP}:{PORT}/api")
    app.run(host='0.0.0.0', port=PORT)

```
