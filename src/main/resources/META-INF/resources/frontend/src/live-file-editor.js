window.fileHandle = null; // Stores the handler for the opened file
window.autoSaveInterval = null;

window.openFile = async function(fileTypes) {
    try {
        const [handle] = await window.showOpenFilePicker(fileTypes);

        fileHandle = handle;

        if(await verifyPermission(fileHandle, true)) {
            const file = await fileHandle.getFile();

            const json = {};
            json.name = file.name;
            json.size = file.size;
            json.type = file.type;
            json.content = await file.text();
            return json;
        } else {
            return {
                'error': 'PERMISSION_NOT_GRANTED',
                'message': 'User did not grant permission'
            };
        }
    } catch (err) {
        const json = {};
        json.error = err.name;
        json.message = err.message;
        return json;
    }
}

async function verifyPermission(fileHandle, withWrite) {
    const opts = {};
    if (withWrite) {
        opts.mode = "readwrite";
    }

    // Check if we already have permission, if so, return true.
    if ((await fileHandle.queryPermission(opts)) === "granted") {
        return true;
    }

    // Request permission to the file, if the user grants permission, return true.
    if ((await fileHandle.requestPermission(opts)) === "granted") {
        return true;
    }

    // The user did not grant permission, return false.
    return false;
}


window.saveFile = async function(content) {
    if(!fileHandle) {
        return {
           "error": 'MISSING_FILE',
           "message": "It's not possible to save because any file was open before"
        }
    }

    try {
        const writable = await fileHandle.createWritable();
        await writable.write(content);
        await writable.close();
        return { "message": "File saved" };
    } catch (err) {
        const json = {};
        json.error = err.name;
        json.message = err.message;
        return json;
    }
}

window.closeFile = async function() {
    if(!fileHandle) {
        return {
           "error": 'MISSING_FILE',
           "message": "There isn't any file to close"
        }
    }

    try {
        fileHandle = null;
        return {}
    } catch (err) {
        const json = {};
        json.error = err.name;
        json.message = err.message;
        return json;
    }
}

// Comienza el auto-guardado cada 5 segundos
window.otherStartAutoSave = function() {
  if (autoSaveInterval) {
    clearInterval(autoSaveInterval);
  }

  autoSaveInterval = setInterval(() => {
    saveFile(document.querySelector('vaadin-text-area').value);
  }, 5000);  // Guardado cada 5 segundos
}

