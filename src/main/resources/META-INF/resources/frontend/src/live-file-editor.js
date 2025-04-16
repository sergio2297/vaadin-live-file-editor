window.fileHandle = null; // Stores the handler for the opened file

window.createFile = async function(fileTypes) {
    try {
        const handle = await window.showSaveFilePicker(fileTypes);

        fileHandle = handle;

        return getFileInfoAsJson(fileHandle);
    } catch (err) {
        return {
            error: err.name,
            message: err.message
        };
    }
}

window.openFile = async function(fileTypes) {
    try {
        const [handle] = await window.showOpenFilePicker(fileTypes);

        fileHandle = handle;

        if(await verifyPermission(fileHandle, true)) {
            return getFileInfoAsJson(fileHandle);
        } else {
            return {
                'error': 'PermissionNotGrantedError',
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

async function getFileInfoAsJson(fileHandle) {
    const file = await fileHandle.getFile();

    const json = {};
    json.name = file.name;
    json.size = file.size;
    json.type = file.type;
    json.content = await file.text();
    return json;
}

window.saveFile = async function(content) {
    if(!fileHandle) {
        return {
           "error": 'MissingFileError',
           "message": "It's not possible to save because no file was open before"
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
           "error": 'MissingFileError',
           "message": "There is no file to close"
        }
    }

    try {
        const file = getFileInfoAsJson(fileHandle);
        fileHandle = null;
        return file;
    } catch (err) {
        const json = {};
        json.error = err.name;
        json.message = err.message;
        return json;
    }
}
