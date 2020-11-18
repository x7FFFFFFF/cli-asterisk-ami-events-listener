//Enables/Disables the do not disturb feature.
function dnd(ext, y) {
    cmd.execute({
        Action: 'DBPut',
        Family: 'DND',
        Key: ext+'',
        Val: y? 'YES': 'NO'
        });
}



