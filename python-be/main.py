from flask import Flask, jsonify, request
from sqlalchemy import create_engine, text

app = Flask(__name__)


class ServicePythonUncommited:
    def __init__(self):
        self.engine = create_engine('mysql://root:password@localhost/dbms_library', isolation_level='READ_UNCOMMITTED')


class ServicePythonCommited:
    def __init__(self):
        self.engine = create_engine('mysql://root:password@localhost/dbms_library', isolation_level='READ_COMMITTED')


serviceUncommited = ServicePythonUncommited()
serviceCommited = ServicePythonCommited()


@app.route('/dirty-write', methods=['POST'])
def dirty_write():
    data = request.get_json()
    id = data.get('id')
    new_name = "transaction2"

    if id is None:
        return jsonify({'error': 'id is required'}), 400

    try:
        with serviceUncommited.engine.connect() as connection:
            query = text("UPDATE artists SET name = 'new_name' WHERE id = :id")
            connection.execute(query, {"id": id})
            connection.commit()
        return new_name, 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/dirty-read', methods=['POST'])
def dirty_read():
    data = request.get_json()
    id = data.get('id')

    if id is None:
        return jsonify({'error': 'id is required'}), 400

    try:
        with serviceUncommited.engine.connect() as connection:
            # perform an update to modify the data between the two reads from the java code
            query = text("UPDATE artists SET followers = followers - 2 WHERE id = :id")
            connection.execute(query, {"id": id})

            # fetch the modified difficulty level from the database
            query_modified = text("SELECT followers FROM artists WHERE id = :id")
            result = connection.execute(query_modified, {"id": id}).fetchone()
            if result:
                modified_followers = result[0]
            else:
                return jsonify({'error': 'No data found for the given id'}), 404

            connection.commit()

        return jsonify({'modified_followers': modified_followers}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/phantom-read', methods=['POST'])
def phantom_read():
    try:
        with serviceCommited.engine.connect() as connection:
            query = text("INSERT INTO artists (id, name, followers) VALUES (4, 'Hob', 11)")
            connection.execute(query)
            connection.commit()
        return jsonify({'success': True}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/lost-update', methods=['POST'])
def lost_update():
    data = request.get_json()
    id = data.get('id')

    if id is None:
        return jsonify({'error': 'id is required'}), 400

    try:
        with serviceUncommited.engine.connect() as connection:
            # perform an update to modify the data between the two reads from the java code
            query = text("UPDATE artists SET followers = followers - 5 WHERE id = :id")
            connection.execute(query, {"id": id})

            # fetch the modified followers count from the database
            query_modified = text("SELECT followers FROM artists WHERE id = :id")
            result = connection.execute(query_modified, {"id": id}).fetchone()
            if result:
                modified_followers = result[0]
            else:
                return jsonify({'error': 'No data found for the given id'}), 404

            connection.commit()

        return jsonify({'modified_followers': modified_followers}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/unrepeatable-reads', methods=['POST'])
def unrepeatable_read():
    data = request.get_json()
    id = data.get('id')

    if id is None:
        return jsonify({'error': 'id is required'}), 400

    try:
        with serviceCommited.engine.connect() as connection:
            # perform an update to modify the data between the two reads from the java code
            query = text("UPDATE artists SET followers = 999 WHERE id = :id")
            connection.execute(query, {"id": id})
            connection.commit()
            # fetch the modified followers count from the database
            query_modified = text("SELECT * FROM artists WHERE id = :id")
            result = connection.execute(query_modified, {"id": id}).fetchone()
            if result:
                print(result)
            else:
                return jsonify({'error': 'No data found for the given id'}), 404

            connection.commit()

        return jsonify({'modified_followers': 999}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500


if __name__ == '__main__':
    app.run(debug=True)
